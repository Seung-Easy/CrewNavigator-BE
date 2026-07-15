package seungeasy.crewnavigator.domain.user.dto;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: UserInfoRow
 *  Description: 사용자 정보 조회 결과를 매핑하는 DTO.
 *  MyBatis SQL 결과를 Java 객체로 변환할 때 사용됩니다.
 *
 *  @param userId    사용자 ID
 *  @param name      사용자 이름
 *  @param email     이메일 주소
 *  @param phone     연락처
 *  @param status    계정 상태 (ACTIVE / INACTIVE / LEAVE)
 *  @param isLocked  계정 잠금 여부 (Y/N)
 *  @param userImage 프로필 이미지 URL
 *  @param createdAt 가입일시
 *  @param deletedAt 탈퇴일시
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record UserInfoRow(
        String userId,
        String name,
        String email,
        String phone,
        String status,
        String isLocked,
        String userImage,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}
