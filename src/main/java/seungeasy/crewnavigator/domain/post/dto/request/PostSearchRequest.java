package seungeasy.crewnavigator.domain.post.dto.request;

/**
 * <pre>
 * Class Name: PostSearchRequest
 * Description: 게시글 다중 조건(카테고리 번호, 제목 키워드) 검색을 위한 Request DTO
 *
 * History
 * 2026.07.07: Chi-Yoon: 게시글 동적 조회를 위한 검색 조건 필드(categoryId, title) 정의
 * </pre>
 *
 * @param categoryId 카테고리 번호 (선택 필터링 조건)
 * @param title      게시글 제목 검색 키워드 (선택 포함 조건)
 * @author Chi-Yoon
 * @version 1.0
 */
public record PostSearchRequest(
        Long categoryId,
        String title
) {
}