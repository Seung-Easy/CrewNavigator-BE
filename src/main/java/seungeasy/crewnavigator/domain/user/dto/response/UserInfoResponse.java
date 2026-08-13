package seungeasy.crewnavigator.domain.user.dto.response;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: UserInfoResponse
 *  Description: 사용자 정보 조회 응답 DTO.
 *  내 정보 조회(/user/profile) 시 반환되는 사용자 프로필 정보입니다.
 *
 *  @param userId    사용자 ID
 *  @param name      사용자 이름
 *  @param email     이메일 주소
 *  @param phone     연락처
 *  @param gender    성별 (M/F/N)
 *  @param userImage 프로필 이미지 URL
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.07.13: Seung-Geon: 도메인 이동(auth -> user)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record UserInfoResponse(
        String userId,
        String name,
        String email,
        String phone,
        String gender,
        String userImage,
        LocalDateTime createdAt
) {}
