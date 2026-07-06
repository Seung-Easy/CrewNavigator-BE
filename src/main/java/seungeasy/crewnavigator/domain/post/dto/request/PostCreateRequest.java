package seungeasy.crewnavigator.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // @NotNull 임포트 추가

/**
 * <pre>
 * Class Name: PostCreateRequest
 * Description: 게시글 생성 요청 DTO. 클라이언트로부터 제목, 본문 내용 및 카테고리 ID를 전달받습니다.
 *
 * [주요 필드]
 * - title: 게시글 제목 (필수)
 * - content: 게시글 본문 (필수)
 * - categoryId: 카테고리 고유 고유 ID (필수)
 *
 * History
 * 2026.06.27: Seung-Geon: 게시글 생성 요청용 DTO 클래스 생성
 * 2026.06.27: Chi-Yoon: 카테고리 제약조건 해결을 위한 categoryId 필드 추가
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
 */
public record PostCreateRequest(
        @NotBlank(message = "게시글 제목은 필수 입력값입니다.")
        String title,

        @NotBlank(message = "게시글 본문은 필수 입력값입니다.")
        String content,

        @NotNull(message = "카테고리 지정은 필수 선택값입니다.")
        Long categoryId
) {}