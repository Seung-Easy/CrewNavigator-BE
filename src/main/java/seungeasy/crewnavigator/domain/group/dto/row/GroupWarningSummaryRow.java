package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: GroupWarningSummaryRow
 *  Description: MyBatis 매핑용 그룹별 경고 누적 집계 Row DTO.
 *  어드민이 모든 그룹의 경고 누적 현황을 파악하기 위한 조회 결과를 담는 중간 객체입니다.
 *
 * History
 * 2026.09.02: Seung-Geon: 어드민 경고 누적 집계 기능을 위해 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class GroupWarningSummaryRow {

    /** 그룹 번호 */
    private Long groupId;

    /** 그룹명 */
    private String groupName;

    /** 그룹장 아이디 */
    private String leaderId;

    /** 누적 경고 수 */
    private Long warningCount;

    /** 가장 최근 경고 일시 */
    private LocalDateTime latestWarningAt;

    /** 가장 최근 경고 사유 */
    private String latestReason;
}
