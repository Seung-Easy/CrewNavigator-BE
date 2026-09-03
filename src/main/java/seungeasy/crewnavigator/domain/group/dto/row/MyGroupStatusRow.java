package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: MyGroupStatusRow
 *  Description: MyBatis 매핑용 나의 그룹 가입 상태 Row DTO.
 *  내 그룹 가입 상태(join_status, leave_reason 등) 조회 결과를 담는 중간 객체입니다.
 *
 * History
 * 2026.09.02: Seung-Geon: 강퇴당한 본인의 사유 조회 기능을 위해 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class MyGroupStatusRow {

    /** 그룹 멤버 매핑 번호 */
    private Long groupMemberId;

    /** 그룹 번호 */
    private Long groupId;

    /** 가입 상태 문자열 (PENDING/APPROVED/REJECTED/INVITED/LEFT) */
    private String joinStatus;

    /** 그룹 내 권한 문자열 (LEADER/MEMBER, null 가능) */
    private String memberRole;

    /** 신청 일시 */
    private LocalDateTime appliedAt;

    /** 나간 사유 (LEFT 상태일 때만 값 존재) */
    private String leaveReason;

    /** 나간 일시 (LEFT 상태일 때만 값 존재) */
    private LocalDateTime leftAt;
}
