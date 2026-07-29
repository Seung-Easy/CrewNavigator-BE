package seungeasy.crewnavigator.domain.auth.service;

import org.springframework.data.domain.Page;
import seungeasy.crewnavigator.domain.auth.dto.request.FindIdRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.AdminUserResponse;
import seungeasy.crewnavigator.domain.user.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.UserStatisticsResponse;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.util.List;
import seungeasy.crewnavigator.domain.auth.dto.response.ActiveSessionResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.LoginHistoryResponse;

/**
 * <pre>
 *  Interface Name: AuthQueryService
 *  Description: 인증/계정 관련 읽기(Query) 작업을 정의한 서비스 인터페이스.
 *
 *  [제공 기능]
 *  - 아이디 찾기
 *  - 내 정보 조회
 *  - 내 로그인 이력 조회
 *  - 관리자용 회원 목록/상세/통계 조회
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 인터페이스 생성
 * 2026.06.16: Seung-Geon: searchUsers, getAdminUserDetail, getUserStatistics 메서드 추가
 * 2026.06.22: Seung-Geon: getMyLoginHistory, getActiveSessions 메서드 추가
 * 2026.06.22: Seung-Geon: getUserInfo 메서드 도메인 변경(auth -> user)
 *
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.3
 */
public interface AuthQueryService {

    /**
     * 이름과 이메일로 가입된 아이디 목록을 조회합니다.
     *
     * @param request 이름과 이메일 정보
     * @return 조회된 아이디 목록
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 일치하는 사용자가 없을 시
     */
    List<String> findUserId(FindIdRequest request);

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (UserInfoResponse)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없을 시
     */
    // UserInfoResponse getUserInfo(String userId);

    /**
     * 관리자용 회원 목록을 검색합니다. (페이징 + 상태/키워드 필터)
     *
     * @param status  필터링할 계정 상태 (null이면 전체)
     * @param keyword 검색어 (userId/name/email LIKE 검색, null이면 전체)
     * @param page    페이지 번호 (0-based)
     * @param size    페이지 크기
     * @return 페이징된 회원 목록
     */
    Page<AdminUserResponse> searchUsers(UserStatus status, String keyword, int page, int size);

    /**
     * 관리자가 특정 사용자의 상세 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 상세 정보 (권한 포함)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없을 시
     */
    AdminUserResponse getAdminUserDetail(String userId);

    /**
     * 회원 통계 정보를 조회합니다. (전체 수, 상태별/권한별 분포, 오늘 가입 수)
     *
     * @return 회원 통계 정보
     */
    UserStatisticsResponse getUserStatistics();

    /**
     * 전체 Role 권한명 목록을 조회합니다.
     *
     * @return Role명 목록 (예: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_OPERATOR", "ROLE_USER"])
     */
    List<String> getAllRoleNames();

    /**
     * 내 로그인 이력을 최신순으로 페이지네이션하여 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @param page   페이지 번호 (0-based)
     * @param size   페이지 크기
     * @return 페이징된 로그인 이력 목록
     */
    Page<LoginHistoryResponse> getMyLoginHistory(String userId, int page, int size);

    /**
     * 현재 로그인 중인(Redis에 refresh token이 있는) 활성 세션 목록을 조회합니다.
     *
     * @return 활성 세션 목록 (userId, 세션 수)
     */
    List<ActiveSessionResponse> getActiveSessions();
}
