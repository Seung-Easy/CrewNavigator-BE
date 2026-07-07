package seungeasy.crewnavigator.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 * Class Name: PostUpdateRequest
 * Description: 게시글 수정 요청 DTO. 클라이언트로부터 수정할 제목과 본문 내용을 전달받습니다.
 *
 * [주요 필드]
 * - title: 수정할 게시글 제목 (필수)
 * - content: 수정할 게시글 본문 (필수)
 *
 * History
 * 2026.07.01: Chi-Yoon: 게시글 수정 요청용 DTO 클래스 최초 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
public record PostUpdateRequest(
        @NotBlank(message = "게시글 제목은 필수 입력값입니다.")
        String title,

        @NotBlank(message = "게시글 본문은 필수 입력값입니다.")
        String content
) {}