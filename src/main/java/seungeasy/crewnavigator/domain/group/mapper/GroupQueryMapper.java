package seungeasy.crewnavigator.domain.group.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import seungeasy.crewnavigator.domain.group.dto.row.ApplicantRow;
import seungeasy.crewnavigator.domain.group.dto.row.GroupRow;
import seungeasy.crewnavigator.domain.group.dto.row.GroupWarningRow;
import seungeasy.crewnavigator.domain.group.dto.row.MemberRow;

import java.util.List;

/**
 * <pre>
 *  Interface Name: GroupQueryMapper
 *  Description: 그룹(모임) 관련 읽기(Query) 작업을 위한 MyBatis Mapper.
 *  GroupQueryServiceImpl에서 JPA 대신 사용됩니다. (CQRS: Query는 MyBatis)
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 조회를 JPA → MyBatis 마이그레이션 (CQRS)
 * 2026.08.29: Seung-Geon: 그룹 경고 목록 조회 메서드 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
@Mapper
public interface GroupQueryMapper {

    /**
     * 삭제되지 않은 그룹 1건을 조회합니다. (없거나 소프트 삭제된 그룹이면 null)
     *
     * @param groupId 그룹 번호
     * @return 그룹 Row (없으면 null)
     */
    GroupRow getGroup(@Param("groupId") Long groupId);

    /**
     * 회원이 가입한(APPROVED) 그룹 목록을 조회합니다.
     *
     * @param userId 회원 아이디
     * @return 가입한 그룹 Row 목록
     */
    List<GroupRow> getMyGroups(@Param("userId") String userId);

    /**
     * 회원이 가입 신청한(PENDING) 그룹 목록을 조회합니다.
     *
     * @param userId 회원 아이디
     * @return 신청 대기 중인 그룹 Row 목록
     */
    List<GroupRow> getMyAppliedGroups(@Param("userId") String userId);

    /**
     * 그룹명 키워드로 공개(is_private="N") 그룹을 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 검색된 그룹 Row 목록
     */
    List<GroupRow> searchGroups(@Param("keyword") String keyword);

    /**
     * 그룹 가입 신청자(PENDING) 목록을 조회합니다. (회원 이름 JOIN 포함)
     *
     * @param groupId 그룹 번호
     * @return 가입 신청자 Row 목록
     */
    List<ApplicantRow> getApplicants(@Param("groupId") Long groupId);

    /**
     * 그룹 멤버(APPROVED) 목록을 조회합니다. (회원 이름/프로필 JOIN 포함)
     *
     * @param groupId 그룹 번호
     * @return 그룹 멤버 Row 목록
     */
    List<MemberRow> getApprovedMembers(@Param("groupId") Long groupId);

    /**
     * 회원이 해당 그룹의 가입(APPROVED) 멤버인지 확인합니다.
     *
     * @param groupId 그룹 번호
     * @param userId  회원 아이디
     * @return 가입 멤버이면 true
     */
    boolean isApprovedMember(@Param("groupId") Long groupId, @Param("userId") String userId);

    /**
     * 특정 그룹의 경고 목록을 조회합니다. (관리자 이름 JOIN 포함)
     *
     * @param groupId 그룹 번호
     * @return 그룹 경고 Row 목록
     */
    List<GroupWarningRow> getGroupWarnings(@Param("groupId") Long groupId);
}
