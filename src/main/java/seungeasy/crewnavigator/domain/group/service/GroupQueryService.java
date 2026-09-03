package seungeasy.crewnavigator.domain.group.service;

import seungeasy.crewnavigator.domain.group.dto.response.ApplicantResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupListResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupWarningResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupWarningSummaryResponse;
import seungeasy.crewnavigator.domain.group.dto.response.InvitationResponse;
import seungeasy.crewnavigator.domain.group.dto.response.MemberResponse;
import seungeasy.crewnavigator.domain.group.dto.response.MyGroupStatusResponse;

import java.util.List;

/**
 * <pre>
 * Interface Name: GroupQueryService
 * Description: 그룹(모임) 관련 읽기/조회(Query) 작업을 처리하는 서비스 인터페이스.
 *
 *  [제공 기능]
 *  - 그룹 상세 조회
 *  - 내가 가입한 그룹 목록 (APPROVED)
 *  - 내가 신청한 그룹 목록 (PENDING)
 *  - 그룹 검색 (그룹명 키워드)
 *  - 그룹 가입 신청자 목록 (그룹장)
 *  - 그룹 멤버 목록 (그룹 멤버)
 *  - 그룹 경고 목록 조회 (그룹 멤버)
 *  - 내가 초대받은 그룹 목록 (INVITED)
 *  - 나의 그룹 가입 상태 조회 (LEFT 사유 포함)
 *  - 그룹별 경고 누적 집계 (어드민)
 *  - 특정 그룹 경고 전체 조회 (어드민)
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 전체 메서드 정의
 * 2026.08.29: Seung-Geon: 목록 조회(검색/내 그룹/신청 그룹) 반환 타입을 GroupListResponse로 변경
 * 2026.08.29: Seung-Geon: 그룹 경고 목록 조회 메서드 추가
 * 2026.09.02: Seung-Geon: 초대 목록/나의 상태/어드민 경고 집계 조회 메서드 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.3
 */
public interface GroupQueryService {

    /**
     * 그룹 상세 정보를 조회합니다. (삭제되지 않은 그룹만)
     *
     * @param groupId 그룹 번호
     * @return 그룹 상세 정보 (멤버 수, 태그 포함)
     */
    GroupResponse getGroup(Long groupId);

    /**
     * 내가 가입한(APPROVED) 그룹 목록을 조회합니다.
     *
     * @param userId 회원 아이디
     * @return 가입한 그룹 목록
     */
    List<GroupListResponse> getMyGroups(String userId);

    /**
     * 내가 가입 신청한(PENDING) 그룹 목록을 조회합니다.
     *
     * @param userId 회원 아이디
     * @return 신청 대기 중인 그룹 목록
     */
    List<GroupListResponse> getMyAppliedGroups(String userId);

    /**
     * 그룹명 키워드로 그룹을 검색합니다.
     * <p>
     * 비공개 그룹(is_private="Y")은 검색 결과에서 제외됩니다.
     *
     * @param keyword 검색 키워드
     * @return 검색된 그룹 목록
     */
    List<GroupListResponse> searchGroups(String keyword);

    /**
     * 그룹 가입 신청자(PENDING) 목록을 조회합니다. (그룹장 전용)
     *
     * @param userId  요청자 회원 아이디 (그룹장 검증용)
     * @param groupId 그룹 번호
     * @return 가입 신청자 목록
     */
    List<ApplicantResponse> getApplicants(String userId, Long groupId);

    /**
     * 그룹 멤버(APPROVED) 목록을 조회합니다. (그룹 멤버 전용)
     *
     * @param userId  요청자 회원 아이디 (멤버 검증용)
     * @param groupId 그룹 번호
     * @return 그룹 멤버 목록
     */
    List<MemberResponse> getGroupMembers(String userId, Long groupId);

    /**
     * 그룹 내 경고 목록을 조회합니다. (그룹 멤버 전용)
     *
     * @param userId  요청자 회원 아이디 (멤버 검증용)
     * @param groupId 그룹 번호
     * @return 그룹 경고 목록
     */
    List<GroupWarningResponse> getGroupWarnings(String userId, Long groupId);

    /**
     * 내가 초대받은(INVITED) 그룹 목록을 조회합니다.
     *
     * @param userId 회원 아이디
     * @return 초대받은 그룹 목록
     */
    List<InvitationResponse> getMyInvitations(String userId);

    /**
     * 특정 그룹에서의 나의 가입 상태를 조회합니다.
     * 강퇴/나가기(LEFT)인 경우 사유(leaveReason)와 일시(leftAt)를 확인할 수 있습니다.
     *
     * @param userId  회원 아이디
     * @param groupId 그룹 번호
     * @return 나의 그룹 가입 상태 정보
     */
    MyGroupStatusResponse getMyGroupStatus(String userId, Long groupId);

    /**
     * 경고가 있는 그룹들의 누적 경고 현황을 집계합니다. (어드민 전용)
     *
     * @return 그룹별 경고 누적 목록 (경고 많은 순)
     */
    List<GroupWarningSummaryResponse> getWarningSummaries();

    /**
     * 특정 그룹의 전체 경고 내역을 조회합니다. (어드민 전용, 멤버 검증 없이 조회)
     *
     * @param groupId 그룹 번호
     * @return 그룹 경고 목록
     */
    List<GroupWarningResponse> getAdminGroupWarnings(Long groupId);
}
