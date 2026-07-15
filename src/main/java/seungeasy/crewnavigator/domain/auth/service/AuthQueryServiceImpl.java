package seungeasy.crewnavigator.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.infra.redis.RedisService;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.FindIdRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.AdminUserResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.ActiveSessionResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.LoginHistoryResponse;
import seungeasy.crewnavigator.domain.user.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.UserStatisticsResponse;
import seungeasy.crewnavigator.domain.auth.dto.row.AdminUserRow;
import seungeasy.crewnavigator.domain.auth.dto.row.RoleCountRow;
import seungeasy.crewnavigator.domain.auth.dto.row.StatusCountRow;
import seungeasy.crewnavigator.domain.auth.entity.LoginHistory;
import seungeasy.crewnavigator.domain.auth.mapper.AuthQueryMapper;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 *  Class Name: AuthQueryServiceImpl
 *  Description: 인증/계정 관련 읽기(Query) 작업을 처리하는 서비스 구현체.
 *
 *  [주요 기능]
 *  - 아이디 찾기
 *  - 내 정보 조회
 *  - 관리자용 회원 목록/상세/통계 조회
 *
 *  [변경 이력]
 *  2026.06.16: Seung-Geon: JPA @Query → MyBatis 마이그레이션 (CQRS)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.15: Seung-Geon: findUserId 이메일 인증코드 검증 로직 추가
 * 2026.06.15: Seung-Geon: findUserId email:verified 키 검증 방식으로 변경 (code 직접 입력 → verify-code 선행)
 * 2026.06.16: Seung-Geon: findUserId email:verified:findid 키로 변경 (용도 구분)
 * 2026.06.16: Seung-Geon: searchUsers, getAdminUserDetail, getUserStatistics 구현
 * 2026.06.16: Seung-Geon: getAllRoleNames 구현 (Role 목록 조회)
 * 2026.06.16: Seung-Geon: JPA @Query → MyBatis 마이그레이션 (CQRS: Query는 MyBatis로 전환)
 * 2026.06.22: Seung-Geon: getMyLoginHistory 메서드 구현 (내 로그인 이력 페이지네이션)
 * 2026.06.22: Seung-Geon: getUserInfo 메서드 도메인 변경(auth -> user)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

    private final AuthQueryMapper authQueryMapper;
    private final RedisService redisService;
    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> findUserId(FindIdRequest request) {
        // 이메일 인증 확인 (findid 용도, verify-code 선행 필수)
        String verifiedKey = "email:verified:findid:" + request.email();
        if (!redisService.hasKey(verifiedKey)) {
            throw new BusinessException(ResponseCode.EMAIL_VERIFICATION_REQUIRED);
        }
        redisService.delete(verifiedKey);

        // 아이디 찾기 (MyBatis)
        String userId = authQueryMapper.findUserId(request.name(), request.email());
        if (userId == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        return List.of(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> searchUsers(UserStatus status, String keyword, int page, int size) {
        String statusStr = status != null ? status.name() : null;

        // 전체 카운트 조회
        long totalCount = authQueryMapper.countUsers(statusStr, keyword);

        // 페이지 데이터 조회
        List<AdminUserRow> rows = authQueryMapper.searchUsers(statusStr, keyword, page * size, size);

        // 변환
        List<AdminUserResponse> content = rows.stream()
                .map(this::toAdminUserResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(content, PageRequest.of(page, size), totalCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getAdminUserDetail(String userId) {
        AdminUserRow row = authQueryMapper.getAdminUserDetail(userId);
        if (row == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        return toAdminUserResponse(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        // 전체 회원 수
        long totalUsers = authQueryMapper.countTotalUsers();

        // 상태별 회원 수
        Map<UserStatus, Long> byStatus = new LinkedHashMap<>();
        for (UserStatus s : UserStatus.values()) {
            byStatus.put(s, 0L);
        }
        List<StatusCountRow> statusCounts = authQueryMapper.countUsersByStatus();
        for (StatusCountRow row : statusCounts) {
            try {
                UserStatus s = UserStatus.valueOf(row.getStatus());
                byStatus.put(s, row.getCount());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown status in DB: {}", row.getStatus());
            }
        }

        // 권한별 회원 수
        Map<String, Long> byRole = new LinkedHashMap<>();
        byRole.put("ROLE_ADMIN", 0L);
        byRole.put("ROLE_MANAGER", 0L);
        byRole.put("ROLE_OPERATOR", 0L);
        byRole.put("ROLE_USER", 0L);
        List<RoleCountRow> roleCounts = authQueryMapper.countUsersByRole();
        for (RoleCountRow row : roleCounts) {
            byRole.put(row.getRoleName(), row.getCount());
        }

        // 오늘 가입자 수
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        long todaySignups = authQueryMapper.countTodaySignups(startOfDay);

        // 전체 가입자 수
        long totalSignups = authQueryMapper.countTotalSignups();

        return new UserStatisticsResponse(totalUsers, byStatus, byRole, todaySignups, totalSignups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllRoleNames() {
        return authQueryMapper.getAllRoleNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActiveSessionResponse> getActiveSessions() {
        Set<String> keys = redisService.keys("refresh:*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        return keys.stream()
                .map(key -> key.substring("refresh:".length()))  // "refresh:userId" → "userId"
                .map(userId -> new ActiveSessionResponse(userId, 1))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LoginHistoryResponse> getMyLoginHistory(String userId, int page, int size) {
        // 전체 이력 조회 (최신순)
        List<LoginHistory> allHistories = loginHistoryRepository.findByUserIdOrderByLoginAtDesc(userId);

        // 전체 카운트
        long totalCount = allHistories.size();

        // 페이지네이션 적용 (서브리스트)
        int start = page * size;
        int end = Math.min(start + size, allHistories.size());
        List<LoginHistoryResponse> content;
        if (start >= allHistories.size()) {
            content = List.of();
        } else {
            List<LoginHistory> pageData = allHistories.subList(start, end);
            content = new ArrayList<>(pageData.size());
            for (int i = 0; i < pageData.size(); i++) {
                LoginHistory h = pageData.get(i);
                content.add(new LoginHistoryResponse(
                        (int) totalCount - start - i,  // seq: 전체 기준 1이 가장 오래된 로그인
                        h.getLoginHistoryId(),
                        h.getLoginAt(),
                        h.getIpAddress(),
                        h.getIsActivated()
                ));
            }
        }

        return new PageImpl<>(content, PageRequest.of(page, size), totalCount);
    }

    /**
     * AdminUserRow를 AdminUserResponse로 변환합니다.
     * roles는 GROUP_CONCAT으로 받은 문자열을 List로 변환합니다.
     */
    private AdminUserResponse toAdminUserResponse(AdminUserRow row) {
        UserStatus userStatus = row.getStatus() != null
                ? UserStatus.valueOf(row.getStatus())
                : null;

        List<String> roleList = row.getRoles() != null && !row.getRoles().isEmpty()
                ? Arrays.asList(row.getRoles().split(","))
                : List.of();

        return new AdminUserResponse(
                row.getUserId(),
                row.getName(),
                row.getEmail(),
                row.getPhone(),
                userStatus,
                row.getIsLocked(),
                roleList,
                row.getCreatedAt(),
                row.getDeletedAt()
        );
    }
}
