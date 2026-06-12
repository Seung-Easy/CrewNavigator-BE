package seungeasy.crewnavigator.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * <pre>
 *  Class Name: SignupRequest
 *  Description: 회원가입 요청 DTO. 신규 사용자 정보를 전달받습니다.
 *
 *  @param userId   사용자 ID (필수)
 *  @param password 비밀번호 (필수, 영문+숫자+특수문자 포함 8~20자)
 *  @param name     이름 (필수)
 *  @param email    이메일 (필수)
 *  @param phone    연락처 (필수, 10~11자리 숫자)
 *  @param birthday 생년월일 (선택, yyyy-MM-dd 형식)
 *  @param address  주소 (선택)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record SignupRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String userId,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "연락처는 필수 입력값입니다.")
        @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다.")
        String phone,

        String birthday,
        String address
) {}
