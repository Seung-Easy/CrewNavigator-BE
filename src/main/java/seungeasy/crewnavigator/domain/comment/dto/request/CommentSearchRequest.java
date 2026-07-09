package seungeasy.crewnavigator.domain.comment.dto.request;

/**
 * <pre>
 * Class Name: CommentSearchRequest
 * Description: 댓글 다중 조건(게시글 번호, 사용자 아이디) 검색을 위한 Request DTO
 *
 * History
 * 2026.07.07: Chi-Yoon: 댓글 동적 조회를 위한 다중 검색 조건 필드(postId, userId) 정의
 * </pre>
 *
 * @param postId 게시글 식별 번호 (선택 필터링 조건)
 * @param userId 사용자 아이디 (선택 필터링 조건)
 * @author Chi-Yoon
 * @version 1.0
 */
public record CommentSearchRequest(
        Long postId,
        String userId
) {
}