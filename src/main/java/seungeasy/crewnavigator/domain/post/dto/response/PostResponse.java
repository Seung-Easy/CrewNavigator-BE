package seungeasy.crewnavigator.domain.post.dto.response;

import java.time.LocalDateTime;
import seungeasy.crewnavigator.domain.post.entity.Post;

/**
 * <pre>
 * Class Name: PostResponse
 * Description: 게시글 다중 조회 결과를 클라이언트에게 반환하기 위한 Response DTO
 *
 * History
 * 2026.07.07: Chi-Yoon: 게시글 피드 및 목록 조회용 응답 필드 정의 및 정적 팩토리 메서드 구현
 * </pre>
 *
 * @param postId     게시글 식별 번호 (PK)
 * @param title      게시글 제목
 * @param content    게시글 본문 내용
 * @param categoryId 연관된 카테고리 식별 번호 (NPE 방지 처리)
 * @param writerId   작성자 유저 아이디
 * @param createdAt  게시글 최초 작성 일시
 * @author Chi-Yoon
 * @version 1.0
 */
public record PostResponse(
        Long postId,
        String title,
        String content,
        Long categoryId,
        String writerId,
        LocalDateTime createdAt
) {
    /**
     * Post 엔티티 객체를 PostResponse DTO 객체로 변환하는 정적 팩토리 메서드입니다.
     * 연관관계가 맺어진 Category 가 null 일 경우를 대비하여 안정성 로직을 포함합니다.
     *
     * @param post 변환할 원본 게시글 엔티티 객체
     * @return 변환이 완료된 PostResponse DTO 인스턴스
     */
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory() != null ? post.getCategory().getId() : null,
                post.getWriter().getUserId(),
                post.getCreatedAt()
        );
    }
}