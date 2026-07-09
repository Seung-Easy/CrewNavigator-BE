package seungeasy.crewnavigator.domain.auth.dto.response;

import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.util.Map;

/**
 * <pre>
 *  Class Name: UserStatisticsResponse
 *  Description: 회원 통계 응답 DTO.
 *  전체 회원 수, 상태별/권한별 분포 등 통계 정보를 반환합니다.
 *
 *  @param totalUsers     전체 회원 수
 *  @param byStatus       상태별 회원 수 (ACTIVE/INACTIVE/LEAVE)
 *  @param byRole         권한별 회원 수 (ROLE_ADMIN/ROLE_MANAGER/ROLE_OPERATOR/ROLE_USER)
 *  @param todaySignups   오늘 가입한 회원 수
 *  @param totalSignups   전체 가입자 수 (탈퇴 포함)
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record UserStatisticsResponse(
        long totalUsers,
        Map<UserStatus, Long> byStatus,
        Map<String, Long> byRole,
        long todaySignups,
        long totalSignups
) {}
