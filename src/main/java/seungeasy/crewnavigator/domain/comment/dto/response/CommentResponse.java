package seungeasy.crewnavigator.domain.comment.dto.response;

import java.time.LocalDateTime;
import seungeasy.crewnavigator.domain.comment.entity.Comment;

/**
 * <pre>
 * Class Name: CommentResponse
 * Description: 댓글 다중 조회 결과를 클라이언트에게 반환하기 위한 Response DTO
 *
 * History
 * 2026.07.07: Chi-Yoon: 댓글 목록 조회용 응답 필드 정의 및 정적 팩토리 메서드 구현
 * </pre>
 *
 * @param commentId 댓글 식별 번호 (PK)
 * @param content   댓글 내용
 * @param postId    연관된 게시글 식별 번호
 * @param writerId  작성자 유저 아이디
 * @param createdAt 댓글 최초 작성 일시
 * @author Chi-Yoon
 * @version 1.0
 */
public record CommentResponse(
        Long commentId,
        String content,
        Long postId,
        String writerId,
        LocalDateTime createdAt
) {
    /**
     * Comment 엔티티 객체를 CommentResponse DTO 객체로 변환하는 정적 팩토리 메서드입니다.
     */
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getWriter().getUserId(),
                comment.getCreatedAt()
        );
    }
}