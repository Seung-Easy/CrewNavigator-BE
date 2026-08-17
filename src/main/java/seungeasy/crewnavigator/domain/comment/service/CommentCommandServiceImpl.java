package seungeasy.crewnavigator.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentCreateRequest;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentUpdateRequest;
import seungeasy.crewnavigator.domain.comment.entity.Comment;
import seungeasy.crewnavigator.domain.comment.repository.CommentRepository;
import seungeasy.crewnavigator.domain.post.entity.Post;
import seungeasy.crewnavigator.domain.post.repository.PostRepository;

/**
 * <pre>
 * Class Name: CommentCommandServiceImpl
 * Description: 댓글(Comment) 관련 쓰기(Command) 작업을 처리하는 서비스 구현체.
 *
 * History
 * 2026.07.05: Chi-Yoon: Post/User 예외 검증 및 댓글 영속성 저장 로직 구현
 * 2026.07.05: Chi-Yoon: 댓글 수정(updateComment) 및 삭제(deleteComment) 권한 검증 로직 추가
 * 2026.07.05: Chi-Yoon: findById 누락 시 INTERNAL_SERVER_ERROR 대신 COMMENT_NOT_FOUND 명확한 예외 코드로 개선
 * 2026.07.25: Chi-Yoon: 관리자 전용 댓글 강제 삭제(forceDeleteCommentByAdmin) 메서드 구현
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 새로운 댓글을 등록하고 데이터베이스에 저장합니다.
     */
    @Override
    @Transactional
    public Long createComment(CommentCreateRequest request, String userId) {
        // 1. 댓글 작성자(User) 존재 여부 검증
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 2. 댓글을 달 대상 게시글(Post) 존재 여부 검증
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));

        // 3. 연관관계 맵핑 정보를 포함하여 댓글 엔티티 빌드
        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .writer(writer)
                .build();

        // 4. 데이터베이스 저장
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully. Comment ID: {} on Post ID: {} by User: {}",
                savedComment.getId(), post.getId(), userId);

        return savedComment.getId();
    }

    /**
     * 기존 댓글을 수정합니다. (🔒 권한 검증)
     */
    @Override
    @Transactional
    public void updateComment(Long commentId, CommentUpdateRequest request, String userId) {
        // 1. 댓글 존재 여부 조회 (💡 COMMENT_NOT_FOUND 명확한 에러 코드 반영)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COMMENT_NOT_FOUND));

        // 2. 🔒 보안 핵심: 댓글 작성자와 현재 로그인 유저 일치 여부 검증
        if (!comment.getWriter().getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_COMMENT_WRITER); // EM001 예외 발생
        }

        // 3. 영속성 컨텍스트 dirty checking을 통한 엔티티 내용 수정
        comment.update(request.content());
        log.info("Comment updated successfully. Comment ID: {} by User: {}", commentId, userId);
    }

    /**
     * 기존 댓글을 삭제(소프트 딜리트)합니다. (🔒 권한 검증)
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId, String userId) {
        // 1. 댓글 존재 여부 조회 (💡 COMMENT_NOT_FOUND 명확한 에러 코드 반영)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COMMENT_NOT_FOUND));

        // 2. 🔒 보안 핵심: 댓글 작성자와 현재 로그인 유저 일치 여부 검증
        if (!comment.getWriter().getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_COMMENT_WRITER); // EM001 예외 발생
        }

        // 3. 엔티티 내 is_deleted 상태를 'Y'로 변경하는 소프트 딜리트 로직 작동
        comment.delete();
        log.info("Comment soft-deleted successfully. Comment ID: {} by User: {}", commentId, userId);
    }

    /**
     * [관리자 전용] 작성자 소유권 검증을 우회하여 댓글을 강제 삭제 처리합니다.
     */
    @Override
    @Transactional
    public void forceDeleteCommentByAdmin(Long commentId, String adminUsername) {
        log.warn("ADMIN ACTION - Force deleting comment ID: {} requested by Admin: {}", commentId, adminUsername);

        // 1. 댓글 존재 여부 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COMMENT_NOT_FOUND));

        // 2. 작성자 검증(NOT_COMMENT_WRITER)을 스킵하고 바로 소프트 딜리트 수행
        comment.delete();
        log.info("Comment force-deleted by Admin successfully. Comment ID: {}, Admin: {}", commentId, adminUsername);
    }
}