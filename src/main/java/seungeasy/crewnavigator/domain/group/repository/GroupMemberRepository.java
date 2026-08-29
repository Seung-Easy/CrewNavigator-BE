package seungeasy.crewnavigator.domain.group.repository;

import seungeasy.crewnavigator.domain.group.entity.GroupMember;
import seungeasy.crewnavigator.domain.group.type.JoinStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: GroupMemberRepository
 * Description: 그룹-회원 가입 관계(GroupMember)에 대한 데이터 접근 인터페이스.
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 생성
 * 2026.08.13: Seung-Geon: LEFT(퇴장) 상태 추가 반영 (Javadoc 갱신)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    /**
     * 그룹과 회원으로 멤버 매핑을 조회합니다. (UK_group_user 기준)
     *
     * @param groupId 그룹 번호
     * @param userId  회원 아이디
     * @return 해당 그룹-회원 매핑 (없으면 empty)
     */
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, String userId);

    /**
     * 그룹 내 특정 가입 상태의 멤버 목록을 조회합니다.
     *
     * @param groupId    그룹 번호
     * @param joinStatus 가입 상태 (PENDING/APPROVED/REJECTED/INVITED)
     * @return 해당 상태의 멤버 목록
     */
    List<GroupMember> findByGroupIdAndJoinStatus(Long groupId, JoinStatus joinStatus);

    /**
     * 회원이 특정 가입 상태에 속한 그룹 멤버 매핑 목록을 조회합니다.
     *
     * @param userId     회원 아이디
     * @param joinStatus 가입 상태 (예: APPROVED → 내 그룹, PENDING → 신청 그룹)
     * @return 해당 상태의 그룹 멤버 매핑 목록
     */
    List<GroupMember> findByUserIdAndJoinStatus(String userId, JoinStatus joinStatus);

    /**
     * 그룹에 속한 전체 멤버 매핑 목록을 조회합니다.
     *
     * @param groupId 그룹 번호
     * @return 전체 멤버 매핑 목록
     */
    List<GroupMember> findByGroupId(Long groupId);

    /**
     * 그룹-회원 매핑 존재 여부를 확인합니다.
     *
     * @param groupId 그룹 번호
     * @param userId  회원 아이디
     * @return 매핑이 존재하면 true
     */
    boolean existsByGroupIdAndUserId(Long groupId, String userId);

    /**
     * 그룹 내 특정 가입 상태의 멤버 수를 집계합니다.
     *
     * @param groupId    그룹 번호
     * @param joinStatus 가입 상태 (정원 체크는 APPROVED 기준)
     * @return 해당 상태의 멤버 수
     */
    long countByGroupIdAndJoinStatus(Long groupId, JoinStatus joinStatus);

    /**
     * 그룹의 모든 멤버 매핑을 삭제합니다. (그룹 해산 시 사용)
     *
     * @param groupId 그룹 번호
     */
    void deleteByGroupId(Long groupId);
}
