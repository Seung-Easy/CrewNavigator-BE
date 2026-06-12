package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * <pre>
 *  Class Name: PasswordChangeRequest
 *  Description: 비밀번호 변경 요청 DTO. 현재 비밀번호와 새 비밀번호를 전달받습니다.
 *
 *  @param currentPassword 현재 비밀번호 (필수)
 *  @param newPassword     새 비밀번호 (필수, 영문+숫자+특수문자 포함 8~20자)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record PasswordChangeRequest(
        @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
        String newPassword
) {}
