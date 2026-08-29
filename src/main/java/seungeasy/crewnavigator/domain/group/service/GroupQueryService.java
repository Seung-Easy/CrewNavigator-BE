package seungeasy.crewnavigator.domain.group.service;

import seungeasy.crewnavigator.domain.group.dto.response.ApplicantResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupListResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupResponse;
import seungeasy.crewnavigator.domain.group.dto.response.MemberResponse;

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
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 전체 메서드 정의
 * 2026.08.29: Seung-Geon: 목록 조회(검색/내 그룹/신청 그룹) 반환 타입을 GroupListResponse로 변경
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
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
}
