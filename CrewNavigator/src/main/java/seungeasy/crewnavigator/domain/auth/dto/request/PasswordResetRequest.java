package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * <pre>
 *  Class Name: PasswordResetRequest
 *  Description: 비밀번호 재설정 요청 DTO. 이메일 인증 완료 후 새 비밀번호를 설정합니다.
 *  이메일 인증(verify-code)이 선행되어야 합니다.
 *
 *  @param userId      사용자 ID (필수)
 *  @param email       이메일 주소 (필수)
 *  @param newPassword 새 비밀번호 (필수, 영문+숫자+특수문자 포함 8~20자)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.15: Seung-Geon: code 필드 추가 (이메일 인증코드)
 * 2026.06.15: Seung-Geon: code 필드 제거, email:verified 키 검증 방식으로 변경
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record PasswordResetRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String userId,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
        String newPassword
) {}
