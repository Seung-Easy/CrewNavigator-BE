package seungeasy.crewnavigator.domain.group.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 * Class Name: GroupWarningRequestDto
 * Description: 그룹 경고 등록 요청을 담는 Request DTO.
 *
 *  [필드]
 *  - warningReason: 경고 사유 (필수)
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 DTO 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupWarningRequestDto(
        @NotBlank(message = "경고 사유를 입력해주세요.")
        String warningReason
) {
}
