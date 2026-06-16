package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * <pre>
 *  Class Name: VerifyCodeRequest
 *  Description: 이메일 인증코드 검증 요청 DTO.
 *  발송 시 사용한 type과 동일한 type으로 검증해야 합니다.
 *
 *  @param email 이메일 주소 (필수)
 *  @param code  인증코드 (필수)
 *  @param type  인증 용도 (필수, signup | findid | reset)
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성
 * 2026.06.16: Seung-Geon: type 필드 추가 (용도 구분)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public record VerifyCodeRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증코드는 필수 입력값입니다.")
        String code,

        @NotBlank(message = "인증 용도는 필수 입력값입니다.")
        @Pattern(regexp = "^(signup|findid|reset|reactivate)$", message = "인증 용도는 signup, findid, reset, reactivate 중 하나여야 합니다.")
        String type
) {}
