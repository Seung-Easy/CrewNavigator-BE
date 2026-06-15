package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 *  Class Name: FindIdRequest
 *  Description: 아이디 찾기 요청 DTO. 이름과 이메일로 가입된 아이디를 조회합니다.
 *  이메일 인증(verify-code)이 선행되어야 합니다.
 *
 *  @param name  사용자 이름 (필수)
 *  @param email 이메일 주소 (필수)
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
public record FindIdRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {}
