package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <pre>
 *  Class Name: ChangeRoleRequest
 *  Description: 회원 권한 변경 요청 DTO.
 *  관리자가 특정 사용자에게 새 권한을 부여할 때 사용합니다.
 *
 *  @param roleName 부여할 권한명 (예: ROLE_MANAGER, ROLE_OPERATOR)
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record ChangeRoleRequest(
        @NotBlank(message = "권한명은 필수 입력값입니다.")
        String roleName
) {}
