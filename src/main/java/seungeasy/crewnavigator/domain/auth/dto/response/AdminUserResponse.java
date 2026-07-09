package seungeasy.crewnavigator.domain.auth.dto.response;

import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 *  Class Name: AdminUserResponse
 *  Description: 관리자 회원 조회 응답 DTO.
 *  회원 목록 조회 및 상세 조회 시 반환되는 사용자 정보입니다.
 *
 *  @param userId     사용자 ID
 *  @param name       사용자 이름
 *  @param email      이메일 주소
 *  @param phone      연락처
 *  @param status     계정 상태 (ACTIVE / INACTIVE / LEAVE)
 *  @param isLocked   계정 잠금 여부 (Y/N)
 *  @param roles      보유 권한 목록 (예: ["ROLE_USER", "ROLE_MANAGER"])
 *  @param createdAt  가입일
 *  @param deletedAt  탈퇴일 (LEAVE 상태일 경우)
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record AdminUserResponse(
        String userId,
        String name,
        String email,
        String phone,
        UserStatus status,
        String isLocked,
        List<String> roles,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {}
