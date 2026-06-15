package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 *  Class Name: VerifyCodeRequest
 *  Description: 이메일 인증코드 검증 요청 DTO.
 *
 *  @param email 이메일 주소 (필수)
 *  @param code  인증코드 (필수)
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record VerifyCodeRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증코드는 필수 입력값입니다.")
        String code
) {}
