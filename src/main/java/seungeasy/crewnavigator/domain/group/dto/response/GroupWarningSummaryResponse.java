package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.GroupWarningSummaryRow;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: GroupWarningSummaryResponse
 * Description: 그룹별 경고 누적 집계 정보를 반환하기 위한 Response DTO. (어드민 전용)
 *
 * History
 * 2026.09.02: Seung-Geon: 어드민 경고 누적 집계 기능을 위해 생성
 * </pre>
 *
 * @param groupId         그룹 번호
 * @param groupName       그룹명
 * @param leaderId        그룹장 아이디
 * @param warningCount    누적 경고 수
 * @param latestWarningAt 가장 최근 경고 일시
 * @param latestReason    가장 최근 경고 사유
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupWarningSummaryResponse(
        Long groupId,
        String groupName,
        String leaderId,
        Long warningCount,
        LocalDateTime latestWarningAt,
        String latestReason
) {
    /**
     * GroupWarningSummaryRow를 GroupWarningSummaryResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param row MyBatis 조회 결과 경고 누적 Row
     * @return 변환이 완료된 GroupWarningSummaryResponse DTO
     */
    public static GroupWarningSummaryResponse from(GroupWarningSummaryRow row) {
        return new GroupWarningSummaryResponse(
                row.getGroupId(),
                row.getGroupName(),
                row.getLeaderId(),
                row.getWarningCount(),
                row.getLatestWarningAt(),
                row.getLatestReason()
        );
    }
}
