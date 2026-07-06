package seungeasy.crewnavigator.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 * Class Name: CommentUpdateRequest
 * Description: 댓글 수정 요청 DTO.
 *
 * [주요 필드]
 * - content: 수정할 댓글 본문 내용 (NOT NULL, 필수)
 *
 * History
 * 2026.07.05: Chi-Yoon: 댓글 수정용 DTO 최초 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
public record CommentUpdateRequest(
        @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
        String content
) {}