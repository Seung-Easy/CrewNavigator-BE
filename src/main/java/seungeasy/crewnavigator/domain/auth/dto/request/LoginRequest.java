package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * <pre>
 *  Class Name: LoginRequest
 *  Description: 로그인 요청 DTO. 사용자 ID와 비밀번호를 전달받습니다.
 *
 *  @param userId   사용자 ID (필수)
 *  @param password 비밀번호 (필수)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record LoginRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String userId,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {}
