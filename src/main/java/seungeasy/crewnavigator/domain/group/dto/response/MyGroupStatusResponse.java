package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.MyGroupStatusRow;
import seungeasy.crewnavigator.domain.group.type.GroupMemberRole;
import seungeasy.crewnavigator.domain.group.type.JoinStatus;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: MyGroupStatusResponse
 * Description: 내 그룹 가입 상태를 반환하기 위한 Response DTO.
 *
 *  - 내가 강퇴/나가기(LEFT)됐을 때 사유(leaveReason)와 일시(leftAt)를 확인할 수 있습니다.
 *  - APPROVED/INVITED/PENDING 등 다른 상태에서도 호출 가능하며, 상태에 따라 사유 필드는 null일 수 있습니다.
 *
 * History
 * 2026.09.02: Seung-Geon: 강퇴당한 본인의 사유 조회 기능을 위해 생성
 * </pre>
 *
 * @param groupMemberId 그룹 멤버 매핑 번호
 * @param groupId       그룹 번호
 * @param joinStatus    가입 상태 (PENDING/APPROVED/REJECTED/INVITED/LEFT)
 * @param memberRole    그룹 내 권한 (LEADER/MEMBER)
 * @param appliedAt     신청 일시
 * @param leaveReason   나간 사유 (LEFT 상태일 때만 값 존재)
 * @param leftAt        나간 일시 (LEFT 상태일 때만 값 존재)
 * @author Seung-Geon
 * @version 1.0
 */
public record MyGroupStatusResponse(
        Long groupMemberId,
        Long groupId,
        JoinStatus joinStatus,
        GroupMemberRole memberRole,
        LocalDateTime appliedAt,
        String leaveReason,
        LocalDateTime leftAt
) {
    /**
     * MyGroupStatusRow를 MyGroupStatusResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param row MyBatis 조회 결과 나의 그룹 상태 Row
     * @return 변환이 완료된 MyGroupStatusResponse DTO
     */
    public static MyGroupStatusResponse from(MyGroupStatusRow row) {
        return new MyGroupStatusResponse(
                row.getGroupMemberId(),
                row.getGroupId(),
                JoinStatus.valueOf(row.getJoinStatus()),
                row.getMemberRole() == null ? GroupMemberRole.MEMBER : GroupMemberRole.valueOf(row.getMemberRole()),
                row.getAppliedAt(),
                row.getLeaveReason(),
                row.getLeftAt()
        );
    }
}
