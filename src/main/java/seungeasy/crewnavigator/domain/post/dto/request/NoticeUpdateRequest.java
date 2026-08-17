package seungeasy.crewnavigator.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 * Record Name: NoticeUpdateRequest
 * Description: 관리자 전용 공지사항 수정 요청 DTO
 *
 * History
 * 2026.07.28: Chi-Yoon: 공지사항 수정 요청 DTO 최초 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Schema(description = "공지사항 수정 요청 DTO")
public record NoticeUpdateRequest(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Schema(description = "수정할 공지사항 제목", example = "[공지] 서비스 점검 일정 변경 안내")
        String title,

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        @Schema(description = "수정할 공지사항 내용", example = "점검 시간이 02:00 ~ 04:00으로 변경되었습니다.")
        String content
) {
}