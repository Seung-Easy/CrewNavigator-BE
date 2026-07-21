package seungeasy.crewnavigator.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentSearchRequest;
import seungeasy.crewnavigator.domain.comment.dto.response.CommentResponse;
import seungeasy.crewnavigator.domain.comment.entity.Comment; // 💡 entity 패키지 경로 지정
import seungeasy.crewnavigator.domain.comment.repository.CommentRepository;

/**
 * <pre>
 * Class Name: CommentQueryServiceImpl
 * Description: 댓글(Comment) 관련 읽기/조회(Query) 작업을 처리하는 서비스 구현체.
 * 성능 최적화를 위해 클래스 레벨에 @Transactional(readOnly = true)을 적용합니다.
 *
 * History
 * 2026.07.07: Chi-Yoon: searchComments 동적 조회 비즈니스 로직 및 DTO 변환 스트림 파이프라인 구현
 * 2026.07.09: Chi-Yoon: 공통 규격 벤치마킹을 위한 페이징 처리(Pageable, Page.map) 로직 구현
 * 2026.07.21: Chi-Yoon: 단건 댓글 상세 조회(getComment) 비즈니스 로직 구현 (💡 추가)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    /**
     * 게시글 번호 및 사용자 아이디를 기반으로 레포지토리에서 데이터를 페이징 조회하고,
     * 응답용 DTO 객체(CommentResponse)로 변환하여 반환합니다.
     */
    @Override
    public Page<CommentResponse> searchComments(CommentSearchRequest request, int page, int size) {
        log.info("Searching comments with paging - PostID: {}, UserID: {}, Page: {}, Size: {}",
                request.postId(), request.userId(), page, size);

        // 1. 대화 흐름 유지를 위해 등록순(ASC) 정렬 조건을 포함한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        // 2. 레포지토리를 호출하여 페이징 쿼리 수행 후, Page.map()을 통해 엔티티를 DTO로 변환하여 반환
        return commentRepository.searchComments(request.postId(), request.userId(), pageable)
                .map(CommentResponse::from);
    }

    /**
     * 특정 댓글 식별 번호(ID)를 기반으로 단건 상세 정보를 조회합니다.
     *
     * @param commentId 조회할 댓글 식별 번호
     * @return 댓글 상세 정보를 담은 응답 DTO
     * @throws IllegalArgumentException 대상 댓글이 존재하지 않을 경우 발생
     */
    @Override
    public CommentResponse getComment(Long commentId) {
        log.info("Fetching comment detail - CommentID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. ID: " + commentId));

        return CommentResponse.from(comment);
    }
}