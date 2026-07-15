package seungeasy.crewnavigator.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * <pre>
 *  Class Name: UserInfoChangeRequest
 *  Description: 사용자 정보 변경 요청 DTO.
 *  내 정보 변경(/user/me PUT) 시 전달되는 요청 본문입니다.
 *
 *  @param name     변경할 이름 (nullable)
 *  @param birthday 변경할 생일 (nullable)
 *  @param address  변경할 주소 (nullable)
 *  @param phone    변경할 연락처 (nullable, 10~11자리 숫자)
 *  @param image    변경할 프로필 이미지 URL (nullable)
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record UserInfoChangeRequest(
        String name,
        LocalDate birthday,
        String address,

        @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다.")
        String phone,
        String image
) { }
