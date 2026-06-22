package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 *  Class Name: ReactivateRequest
 *  Description: 탈퇴(LEAVE) → 비활성(INACTIVE) 상태로 복구된 계정을
 *  이메일 인증을 통해 다시 활성(ACTIVE) 상태로 전환할 때 사용하는 요청 DTO.
 *
 *  @param email 복구할 계정의 이메일 주소 (필수)
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record ReactivateRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {}
