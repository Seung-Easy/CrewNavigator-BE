package seungeasy.crewnavigator.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.group.dto.response.ApplicantResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupResponse;
import seungeasy.crewnavigator.domain.group.dto.response.MemberResponse;
import seungeasy.crewnavigator.domain.group.dto.row.GroupRow;
import seungeasy.crewnavigator.domain.group.mapper.GroupQueryMapper;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * Class Name: GroupQueryServiceImpl
 * Description: 그룹(모임) 관련 읽기/조회(Query) 작업을 처리하는 서비스 구현체.
 * 조회는 JPA가 아닌 MyBatis(GroupQueryMapper)를 사용합니다. (CQRS: Query는 MyBatis)
 * 그룹 상세 조회 시 멤버 수·태그는 SQL 서브쿼리로 함께 조회하여 N+1 문제를 제거합니다.
 *
 * History
 * 2026.08.02: Seung-Geon: 스텁 서비스를 그룹 도메인 확장에 맞춰 전체 구현
 * 2026.08.02: Seung-Geon: JPA → MyBatis 마이그레이션 (CQRS, GroupQueryMapper 적용)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupQueryServiceImpl implements GroupQueryService {

    private final GroupQueryMapper groupQueryMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroup(Long groupId) {
        GroupRow row = getActiveGroup(groupId);
        return toResponse(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getMyGroups(String userId) {
        return groupQueryMapper.getMyGroups(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getMyAppliedGroups(String userId) {
        return groupQueryMapper.getMyAppliedGroups(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> searchGroups(String keyword) {
        // is_private="Y"(비공개) 그룹은 SQL에서 제외 (is_private = 'N')
        return groupQueryMapper.searchGroups(keyword).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicantResponse> getApplicants(String userId, Long groupId) {
        GroupRow group = getActiveGroup(groupId);
        validateLeader(group, userId);

        return groupQueryMapper.getApplicants(groupId).stream()
                .map(ApplicantResponse::from)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getGroupMembers(String userId, Long groupId) {
        GroupRow group = getActiveGroup(groupId);
        validateMember(group, userId);

        return groupQueryMapper.getApprovedMembers(groupId).stream()
                .map(MemberResponse::from)
                .toList();
    }

    // =================================================================
    // 내부 헬퍼 메서드
    // =================================================================

    /**
     * 삭제되지 않은 그룹을 조회합니다. 없거나 소프트 삭제된 그룹이면 GROUP_NOT_FOUND 예외를 던집니다.
     */
    private GroupRow getActiveGroup(Long groupId) {
        GroupRow group = groupQueryMapper.getGroup(groupId);
        if (group == null) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
        return group;
    }

    /**
     * 요청자가 해당 그룹의 그룹장인지 검증합니다.
     */
    private void validateLeader(GroupRow group, String userId) {
        if (!group.getLeaderId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_GROUP_LEADER);
        }
    }

    /**
     * 요청자가 해당 그룹의 가입(APPROVED) 멤버인지 검증합니다.
     */
    private void validateMember(GroupRow group, String userId) {
        if (!groupQueryMapper.isApprovedMember(group.getGroupId(), userId)) {
            throw new BusinessException(ResponseCode.NOT_GROUP_MEMBER);
        }
    }

    /**
     * GroupRow를 GroupResponse DTO로 변환합니다. (태그 목록 분리 포함)
     */
    private GroupResponse toResponse(GroupRow row) {
        return GroupResponse.from(row, splitTags(row.getTags()));
    }

    /**
     * GROUP_CONCAT으로 받은 태그 문자열을 List로 변환합니다. (null/빈 문자열 → 빈 목록)
     */
    private List<String> splitTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .toList();
    }
}
