package seungeasy.crewnavigator.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공지사항 등록 요청 DTO")
public record NoticeCreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Schema(description = "공지사항 제목", example = "[공지] 시스템 점검 안내")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        @Schema(description = "공지사항 내용", example = "서비스 점검 예정입니다.")
        String content
) {
}