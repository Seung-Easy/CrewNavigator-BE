package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * <pre>
 *  Class Name: SendVerificationCodeRequest
 *  Description: 이메일 인증코드 전송 요청 DTO.
 *  type에 따라 Redis 키가 분리되어 인증 용도가 구분됩니다.
 *  (signup → 회원가입, findid → 아이디 찾기)
 *
 *  @param email 이메일 주소 (필수)
 *  @param type  인증 용도 (필수, signup | findid)
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성
 * 2026.06.16: Seung-Geon: type 필드 추가 (용도 구분)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public record SendVerificationCodeRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증 용도는 필수 입력값입니다.")
        @Pattern(regexp = "^(signup|findid|reactivate)$", message = "인증 용도는 signup, findid, reactivate 중 하나여야 합니다.")
        String type
) {}
