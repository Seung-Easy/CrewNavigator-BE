package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 *  Class Name: SendResetCodeRequest
 *  Description: 비밀번호 재설정 인증코드 전송 요청 DTO.
 *  아이디를 알고 있는 사용자가 비밀번호 재설정을 위해 인증코드를 요청할 때 사용합니다.
 *  (아이디 찾기와 달리 userId를 입력받아 본인 확인을 강화합니다.)
 *
 *  @param userId 사용자 ID (필수)
 *  @param email  이메일 주소 (필수)
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record SendResetCodeRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String userId,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {}
