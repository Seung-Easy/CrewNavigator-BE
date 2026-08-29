package seungeasy.crewnavigator.domain.group.service;

import seungeasy.crewnavigator.domain.group.dto.request.GroupCreateRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupInviteRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupMemberRemoveRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupUpdateRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupWarningRequestDto;
import seungeasy.crewnavigator.domain.group.type.GroupMemberRole;

/**
 * <pre>
 * Interface Name: GroupCommandService
 * Description: 그룹(모임) 관련 쓰기(Command) 작업을 처리하는 서비스 인터페이스.
 *
 *  [제공 기능]
 *  - 그룹 생성 / 정보 수정 / 소프트 삭제 (그룹장)
 *  - 그룹원 초대 / 추방 (그룹장)
 *  - 가입 신청 승인 / 거절 (그룹장)
 *  - 멤버 권한 변경 및 그룹장 위임 (그룹장)
 *  - 가입 신청 / 신청 취소 (멤버)
 *  - 그룹 나가기 (멤버)
 *  - 초대 수락 / 거절 (초대받은 멤버)
 *
 * History
 * 2026.07.24: Seung-Geon: 인터페이스 생성
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 전체 메서드 정의
 * 2026.08.29: Seung-Geon: 그룹 경고 부여 메서드 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.3
 */
public interface GroupCommandService {

    /**
     * 그룹을 생성합니다.
     * <p>
     * 생성자는 자동으로 그룹장(LEADER)이 되며, 그룹 태그가 함께 등록됩니다.
     *
     * @param userId  생성자(그룹장) 회원 아이디
     * @param request 그룹 생성 요청 정보
     */
    void createGroup(String userId, GroupCreateRequestDto request);

    /**
     * 그룹원을 초대합니다. 초대받은 회원은 INVITED 상태가 됩니다.
     *
     * @param userId  그룹장 회원 아이디
     * @param groupId 대상 그룹 번호
     * @param request 초대할 회원 아이디 목록
     */
    void inviteMember(String userId, Long groupId, GroupInviteRequestDto request);

    /**
     * 그룹원을 추방합니다. (그룹장은 추방할 수 없음)
     *
     * @param userId  그룹장 회원 아이디
     * @param groupId 대상 그룹 번호
     * @param request 추방할 회원 아이디
     */
    void removeMember(String userId, Long groupId, GroupMemberRemoveRequestDto request);

    /**
     * 가입 신청을 승인합니다. (그룹장 전용, 정원 초과 시 거절)
     *
     * @param userId        그룹장 회원 아이디
     * @param groupId       대상 그룹 번호
     * @param groupMemberId 승인할 신청자의 멤버 매핑 번호
     */
    void approveApplicant(String userId, Long groupId, Long groupMemberId);

    /**
     * 가입 신청을 거절합니다. (그룹장 전용)
     *
     * @param userId        그룹장 회원 아이디
     * @param groupId       대상 그룹 번호
     * @param groupMemberId 거절할 신청자의 멤버 매핑 번호
     */
    void rejectApplicant(String userId, Long groupId, Long groupMemberId);

    /**
     * 그룹을 소프트 삭제(해산)합니다. (그룹장 전용)
     * 멤버 매핑과 태그 매핑은 함께 정리됩니다.
     *
     * @param userId  그룹장 회원 아이디
     * @param groupId 대상 그룹 번호
     */
    void deleteGroup(String userId, Long groupId);

    /**
     * 그룹 정보를 수정합니다. (그룹장 전용)
     * <p>
     * 요청 값이 null이 아닌 필드만 갱신되며, 태그 목록이 전달되면 기존 태그를 교체합니다.
     *
     * @param userId  그룹장 회원 아이디
     * @param groupId 대상 그룹 번호
     * @param request 수정할 그룹 정보
     */
    void updateGroup(String userId, Long groupId, GroupUpdateRequestDto request);

    /**
     * 멤버 권한을 변경합니다. (그룹장 전용)
     * <p>
     * 다른 멤버를 LEADER로 승격하면 그룹장이 위임됩니다 (기존 그룹장은 MEMBER로 강등,
     * crew_group.leader_id 갱신). 현재 그룹장을 MEMBER로 강등하는 것은 불가합니다.
     *
     * @param userId      그룹장 회원 아이디
     * @param groupId     대상 그룹 번호
     * @param targetUserId 권한을 변경할 대상 회원 아이디
     * @param role        변경할 권한 (LEADER, MEMBER)
     */
    void changeMemberRole(String userId, Long groupId, String targetUserId, GroupMemberRole role);

    /**
     * 그룹에 가입을 신청합니다.
     * <p>
     * 모든 가입 신청은 그룹장의 승인을 기다리는 PENDING 상태가 됩니다.
     * 거절(REJECTED)된 신청은 재신청 시 다시 신청 상태로 전환됩니다.
     * (is_private는 그룹 검색 노출 여부를 나타내는 설정으로, 가입 승인 방식과 무관합니다)
     *
     * @param userId  신청자 회원 아이디
     * @param groupId 대상 그룹 번호
     */
    void applyToGroup(String userId, Long groupId);

    /**
     * 가입 신청을 취소합니다. (PENDING 상태만 취소 가능)
     * 이미 가입(APPROVED)된 경우 그룹 나가기를 이용해야 합니다.
     *
     * @param userId  신청자 회원 아이디
     * @param groupId 대상 그룹 번호
     */
    void cancelGroupApplication(String userId, Long groupId);

    /**
     * 그룹에서 나갑니다. (그룹장은 나갈 수 없음 — 위임 후 가능)
     * <p>
     * 매핑 행을 삭제하지 않고 LEFT 상태로 전환하여 나간 사유와 일시를 기록합니다.
     * 이후 재신청하면 이전 사유가 그룹장에게 노출되며, 승인 시 초기화됩니다.
     *
     * @param userId      탈퇴할 회원 아이디
     * @param groupId     대상 그룹 번호
     * @param leaveReason 나간 사유 (선택 — 없으면 null)
     */
    void leaveGroup(String userId, Long groupId, String leaveReason);

    /**
     * 그룹 초대에 응답합니다. (초대받은 본인만 가능)
     *
     * @param userId        응답자(초대받은) 회원 아이디
     * @param groupMemberId 초대 매핑 번호 (INVITED 상태)
     * @param accept        true면 수락, false면 거절
     */
    void respondToInvitation(String userId, Long groupMemberId, boolean accept);

    /**
     * 관리자가 특정 그룹에 경고를 부여합니다.
     *
     * @param adminId 관리자 아이디
     * @param groupId 대상 그룹 번호
     * @param request 경고 등록 요청 정보
     */
    void warnGroup(String adminId, Long groupId, GroupWarningRequestDto request);
}
