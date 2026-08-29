package seungeasy.crewnavigator.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;
import seungeasy.crewnavigator.domain.group.dto.request.GroupCreateRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupInviteRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupMemberRemoveRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupUpdateRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupWarningRequestDto;
import seungeasy.crewnavigator.domain.group.entity.CrewGroup;
import seungeasy.crewnavigator.domain.group.entity.GroupMember;
import seungeasy.crewnavigator.domain.group.entity.GroupTag;
import seungeasy.crewnavigator.domain.group.entity.GroupWarning;
import seungeasy.crewnavigator.domain.group.entity.Tag;
import seungeasy.crewnavigator.domain.group.entity.id.GroupTagId;
import seungeasy.crewnavigator.domain.group.repository.GroupMemberRepository;
import seungeasy.crewnavigator.domain.group.repository.GroupRepository;
import seungeasy.crewnavigator.domain.group.repository.GroupTagRepository;
import seungeasy.crewnavigator.domain.group.repository.GroupWarningRepository;
import seungeasy.crewnavigator.domain.group.repository.TagRepository;
import seungeasy.crewnavigator.domain.group.type.GroupMemberRole;
import seungeasy.crewnavigator.domain.group.type.JoinStatus;

import java.util.Optional;

/**
 * <pre>
 * Class Name: GroupCommandServiceImpl
 * Description: 그룹(모임) 관련 쓰기(Command) 작업을 처리하는 서비스 구현체.
 *
 *  [비즈니스 규칙]
 *  - 그룹 생성자는 자동으로 그룹장(LEADER) + 가입(APPROVED) 처리
 *  - 그룹 가입 신청은 모두 그룹장 승인 대기(PENDING) 상태 (즉시 가입 없음)
 *  - is_private는 "그룹 검색 노출 여부"를 나타내는 설정으로, 가입 승인 방식과 무관함
 *  - 거절(REJECTED)된 신청은 재신청 시 PENDING으로 재전환 (UK_group_user 제약 때문)
 *  - 나가기/추방 시 매핑 행을 삭제하지 않고 LEFT 상태로 전환 (soft-delete)
 *    → 재신청 시 LEFT → PENDING으로 재전환되며, 이전 나간 사유는 승인 시 초기화됨
 *  - 그룹장은 나가기/강등 불가 — 위임(다른 멤버를 LEADER로 승격)으로만 변경 가능
 *  - 정원(max_members)은 APPROVED 멤버 수 기준으로 체크
 *  - 나간(LEFT)/거절(REJECTED)된 회원은 재초대 시 INVITED로 재전환
 *
 * History
 * 2026.08.02: Seung-Geon: 스텁 서비스를 그룹 도메인 확장에 맞춰 전체 구현
 * 2026.08.13: Seung-Geon: 나가기/추방 soft-delete 전환 (LEFT 상태, leave_reason/left_at 기록)
 * 2026.08.29: Seung-Geon: LEFT/REJECTED 회원 재초대 처리
 * 2026.08.29: Seung-Geon: 그룹 경고 부여 기능 구현
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupCommandServiceImpl implements GroupCommandService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TagRepository tagRepository;
    private final GroupTagRepository groupTagRepository;
    private final UserRepository userRepository;
    private final GroupWarningRepository groupWarningRepository;

    @Override
    @Transactional
    public void createGroup(String userId, GroupCreateRequestDto request) {
        // 1. 생성자 회원 존재 확인 (FK 제약 대응)
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 2. 그룹 저장 (기본값은 엔티티 @PrePersist에서 처리)
        CrewGroup group = new CrewGroup();
        group.setLeaderId(userId);
        group.setGroupName(request.groupName());
        group.setDescription(request.description());
        group.setMaxMembers(request.maxMembers());
        group.setIsPrivate(request.isPrivate());
        group.setGroupImage(request.groupImage());
        CrewGroup saved = groupRepository.save(group);

        // 3. 태그 등록 (있으면)
        if (request.tags() != null) {
            replaceTags(saved, request.tags());
        }

        // 4. 그룹장 멤버 매핑 생성 (LEADER + APPROVED)
        GroupMember leader = GroupMember.createPending(saved.getGroupId(), userId);
        leader.approve();
        leader.changeRole(GroupMemberRole.LEADER);
        groupMemberRepository.save(leader);

        log.info("Group created successfully. Group ID: {}, Leader: {}", saved.getGroupId(), userId);
    }

    @Override
    @Transactional
    public void inviteMember(String userId, Long groupId, GroupInviteRequestDto request) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        if (request.userIds() == null || request.userIds().isEmpty()) {
            return;
        }

        for (String invitedUserId : request.userIds()) {
            if (invitedUserId == null || invitedUserId.isBlank()) {
                continue;
            }
            if (invitedUserId.equals(userId)) {
                throw new BusinessException(ResponseCode.CANNOT_INVITE_SELF);
            }
            // 초대 대상 회원 존재 확인
            userRepository.findById(invitedUserId)
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            // 기존 멤버 매핑 존재 여부에 따라 신규 초대 또는 재초대 처리
            Optional<GroupMember> existing = groupMemberRepository.findByGroupIdAndUserId(groupId, invitedUserId);
            if (existing.isPresent()) {
                reInvite(groupId, existing.get());
            } else {
                groupMemberRepository.save(GroupMember.createInvited(groupId, invitedUserId));
            }
        }
        log.info("Members invited successfully. Group ID: {}, Invited by: {}", groupId, userId);
    }

    @Override
    @Transactional
    public void removeMember(String userId, Long groupId, GroupMemberRemoveRequestDto request) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, request.userId())
                .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND));

        // 그룹장은 추방 불가 (위임을 통해서만 변경)
        if (target.getMemberRole() == GroupMemberRole.LEADER) {
            throw new BusinessException(ResponseCode.GROUP_LEADER_CANNOT_DEMOTE);
        }

        // 추방도 soft-delete: LEFT 상태로 전환하여 사유와 일시 기록 (재신청 시 이력 확인 가능)
        target.leave(request.leaveReason());
        log.info("Member removed successfully. Group ID: {}, Removed User: {}", groupId, request.userId());
    }

    @Override
    @Transactional
    public void approveApplicant(String userId, Long groupId, Long groupMemberId) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        GroupMember applicant = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND));
        if (!applicant.getGroupId().equals(groupId)) {
            throw new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND);
        }
        if (applicant.getJoinStatus() != JoinStatus.PENDING) {
            throw new BusinessException(ResponseCode.INVALID_JOIN_STATUS);
        }

        checkCapacity(group);
        applicant.approve();
        log.info("Applicant approved. Group ID: {}, User: {}", groupId, applicant.getUserId());
    }

    @Override
    @Transactional
    public void rejectApplicant(String userId, Long groupId, Long groupMemberId) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        GroupMember applicant = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND));
        if (!applicant.getGroupId().equals(groupId)) {
            throw new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND);
        }
        if (applicant.getJoinStatus() != JoinStatus.PENDING) {
            throw new BusinessException(ResponseCode.INVALID_JOIN_STATUS);
        }

        applicant.reject();
        log.info("Applicant rejected. Group ID: {}, User: {}", groupId, applicant.getUserId());
    }

    @Override
    @Transactional
    public void deleteGroup(String userId, Long groupId) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        group.delete();
        // 해산된 그룹의 멤버/태그/경고 매핑 정리
        groupMemberRepository.deleteByGroupId(groupId);
        groupTagRepository.deleteByGroupTagId_GroupId(groupId);
        groupWarningRepository.deleteByGroupId(groupId);

        log.info("Group soft-deleted successfully. Group ID: {}, Deleted by: {}", groupId, userId);
    }

    @Override
    @Transactional
    public void updateGroup(String userId, Long groupId, GroupUpdateRequestDto request) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        group.update(
                request.groupName(),
                request.description(),
                request.maxMembers(),
                request.isPrivate(),
                request.groupImage()
        );
        // 태그 목록이 전달되면 기존 태그를 교체
        if (request.tags() != null) {
            replaceTags(group, request.tags());
        }

        log.info("Group updated successfully. Group ID: {}, Updated by: {}", groupId, userId);
    }

    @Override
    @Transactional
    public void changeMemberRole(String userId, Long groupId, String targetUserId, GroupMemberRole role) {
        CrewGroup group = getActiveGroup(groupId);
        validateLeader(group, userId);

        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND));

        // 현재 그룹장 강등 차단 (위임으로만 변경 가능)
        if (target.getMemberRole() == GroupMemberRole.LEADER && role == GroupMemberRole.MEMBER) {
            throw new BusinessException(ResponseCode.GROUP_LEADER_CANNOT_DEMOTE);
        }

        if (role == GroupMemberRole.LEADER && target.getMemberRole() != GroupMemberRole.LEADER) {
            // 그룹장 위임: 기존 그룹장 → MEMBER, 대상 → LEADER, crew_group.leader_id 갱신
            GroupMember oldLeader = groupMemberRepository.findByGroupIdAndUserId(groupId, group.getLeaderId())
                    .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND));
            oldLeader.changeRole(GroupMemberRole.MEMBER);
            target.changeRole(GroupMemberRole.LEADER);
            group.setLeaderId(targetUserId);
        } else {
            target.changeRole(role);
        }

        log.info("Member role changed. Group ID: {}, Target User: {}, Role: {}", groupId, targetUserId, role);
    }

    @Override
    @Transactional
    public void applyToGroup(String userId, Long groupId) {
        CrewGroup group = getActiveGroup(groupId);

        Optional<GroupMember> existing = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (existing.isPresent()) {
            GroupMember member = existing.get();
            switch (member.getJoinStatus()) {
                case APPROVED -> throw new BusinessException(ResponseCode.ALREADY_GROUP_MEMBER);
                case PENDING -> throw new BusinessException(ResponseCode.ALREADY_GROUP_APPLIED);
                case INVITED -> throw new BusinessException(ResponseCode.INVALID_JOIN_STATUS);
                case REJECTED -> {
                    // 거절된 신청 재신청: PENDING으로 재전환
                    member.reapply();
                    log.info("Group application reapplied. Group ID: {}, User: {}", groupId, userId);
                    return;
                }
                case LEFT -> {
                    // 퇴장(LEFT)했던 멤버의 재신청: PENDING으로 재전환
                    // 이전 나간 사유(leaveReason/leftAt)는 승인 시까지 보존되어 그룹장이 확인 가능
                    member.reapply();
                    log.info("Group application reapplied after leave. Group ID: {}, User: {}", groupId, userId);
                    return;
                }
            }
        }

        // 모든 가입 신청은 그룹장 승인 대기(PENDING) 상태 (is_private와 무관)
        GroupMember member = GroupMember.createPending(groupId, userId);
        groupMemberRepository.save(member);
        log.info("Group application submitted. Group ID: {}, User: {}, Status: {}",
                groupId, userId, member.getJoinStatus());
    }

    @Override
    @Transactional
    public void cancelGroupApplication(String userId, Long groupId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_MEMBER_NOT_FOUND));

        if (member.getJoinStatus() == JoinStatus.APPROVED) {
            throw new BusinessException(ResponseCode.ALREADY_GROUP_MEMBER);
        }
        if (member.getJoinStatus() != JoinStatus.PENDING) {
            throw new BusinessException(ResponseCode.INVALID_JOIN_STATUS);
        }

        groupMemberRepository.delete(member);
        log.info("Group application cancelled. Group ID: {}, User: {}", groupId, userId);
    }

    @Override
    @Transactional
    public void leaveGroup(String userId, Long groupId, String leaveReason) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_GROUP_MEMBER));

        if (member.getMemberRole() == GroupMemberRole.LEADER) {
            throw new BusinessException(ResponseCode.GROUP_LEADER_CANNOT_LEAVE);
        }
        if (member.getJoinStatus() != JoinStatus.APPROVED) {
            throw new BusinessException(ResponseCode.NOT_GROUP_MEMBER);
        }

        // 나가기 = soft-delete: LEFT 상태로 전환하여 사유와 일시 기록
        // (재신청 시 UK_group_user 제약 충돌 없이 LEFT → PENDING으로 재전환)
        member.leave(leaveReason);
        log.info("Member left group. Group ID: {}, User: {}, Reason: {}", groupId, userId, leaveReason);
    }

    @Override
    @Transactional
    public void respondToInvitation(String userId, Long groupMemberId, boolean accept) {
        GroupMember member = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.INVITATION_NOT_FOUND));

        // 초대받은 본인만 응답 가능 (타인 응답 시 정보 노출 방지를 위해 동일 예외 반환)
        if (!member.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.INVITATION_NOT_FOUND);
        }
        if (member.getJoinStatus() != JoinStatus.INVITED) {
            throw new BusinessException(ResponseCode.INVALID_JOIN_STATUS);
        }

        if (accept) {
            CrewGroup group = getActiveGroup(member.getGroupId());
            checkCapacity(group);
            member.acceptInvitation();
            log.info("Invitation accepted. Group ID: {}, User: {}", member.getGroupId(), userId);
        } else {
            member.declineInvitation();
            log.info("Invitation declined. Group ID: {}, User: {}", member.getGroupId(), userId);
        }
    }

    // =================================================================
    // 내부 헬퍼 메서드
    // =================================================================

    /**
     * 삭제되지 않은 그룹을 조회합니다. 없거나 소프트 삭제된 그룹이면 GROUP_NOT_FOUND 예외를 던집니다.
     */
    private CrewGroup getActiveGroup(Long groupId) {
        CrewGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ResponseCode.GROUP_NOT_FOUND));
        if ("Y".equals(group.getIsDeleted())) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
        return group;
    }

    /**
     * 요청자가 해당 그룹의 그룹장인지 검증합니다.
     */
    private void validateLeader(CrewGroup group, String userId) {
        if (!group.getLeaderId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_GROUP_LEADER);
        }
    }

    /**
     * 그룹 정원(max_members)을 초과했는지 검증합니다. (APPROVED 멤버 수 기준)
     */
    private void checkCapacity(CrewGroup group) {
        long approvedCount = groupMemberRepository.countByGroupIdAndJoinStatus(group.getGroupId(), JoinStatus.APPROVED);
        if (approvedCount >= group.getMaxMembers()) {
            throw new BusinessException(ResponseCode.GROUP_IS_FULL);
        }
    }

    /**
     * 태그 이름 목록을 find-or-create 방식으로 그룹에 매핑합니다.
     * 기존 그룹 태그 매핑은 모두 제거한 뒤 새로 등록합니다 (교체).
     */
    private void replaceTags(CrewGroup group, java.util.List<String> tagNames) {
        groupTagRepository.deleteByGroupTagId_GroupId(group.getGroupId());

        for (String tagName : tagNames) {
            if (tagName == null || tagName.isBlank()) {
                continue;
            }
            Tag tag = findOrCreateTag(tagName.trim());
            groupTagRepository.save(new GroupTag(
                    new GroupTagId(group.getGroupId(), tag.getTagId()),
                    group,
                    tag
            ));
        }
    }

    /**
     * 태그 이름으로 조회하고, 없으면 새로 생성하여 반환합니다. (tag_name UNIQUE)
     */
    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByTagName(tagName)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setTagName(tagName);
                    return tagRepository.save(tag);
                });
    }

    /**
     * 이미 그룹-회원 매핑이 존재하는 경우의 초대 처리입니다.
     * <p>
     * - APPROVED: 이미 가입한 멤버 → ALREADY_GROUP_MEMBER
     * - PENDING:   가입 신청 대기 중 → ALREADY_GROUP_APPLIED
     * - INVITED:   이미 초대 중 → ALREADY_INVITED
     * - LEFT:      나갔던/강퇴된 멤버 재초대 → invite()로 INVITED 재전환
     * - REJECTED:  거절된 신청 재초대 → invite()로 INVITED 재전환
     */
    private void reInvite(Long groupId, GroupMember member) {
        switch (member.getJoinStatus()) {
            case APPROVED -> throw new BusinessException(ResponseCode.ALREADY_GROUP_MEMBER);
            case PENDING -> throw new BusinessException(ResponseCode.ALREADY_GROUP_APPLIED);
            case INVITED -> throw new BusinessException(ResponseCode.ALREADY_INVITED);
            case LEFT, REJECTED -> {
                member.invite();
                log.info("Member re-invited. Group ID: {}, User: {}", groupId, member.getUserId());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void warnGroup(String adminId, Long groupId, GroupWarningRequestDto request) {
        CrewGroup group = getActiveGroup(groupId);
        groupWarningRepository.save(GroupWarning.create(groupId, adminId, request.warningReason()));
        log.info("Group warned successfully. Group ID: {}, Warned by: {}", groupId, adminId);
    }
}
