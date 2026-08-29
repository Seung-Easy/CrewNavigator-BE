package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.entity.GroupWarning;
import seungeasy.crewnavigator.domain.group.dto.row.GroupWarningRow;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: GroupWarningResponse
 * Description: 그룹 경고 정보를 반환하기 위한 Response DTO.
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 DTO 생성
 * </pre>
 *
 * @param groupWarningId 그룹 경고 번호
 * @param groupId        그룹 번호
 * @param adminId        경고를 부여한 관리자 아이디
 * @param adminName      경고를 부여한 관리자 이름
 * @param warningReason  경고 사유
 * @param createdAt      경고 일시
 * @author Seung-Geon
 * @version 1.1
 */
public record GroupWarningResponse(
        Long groupWarningId,
        Long groupId,
        String adminId,
        String adminName,
        String warningReason,
        LocalDateTime createdAt
) {
    /**
     * GroupWarning 엔티티를 GroupWarningResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param entity 그룹 경고 엔티티
     * @return 변환이 완료된 GroupWarningResponse DTO
     */
    public static GroupWarningResponse from(GroupWarning entity) {
        return new GroupWarningResponse(
                entity.getGroupWarningId(),
                entity.getGroupId(),
                entity.getAdminId(),
                null,
                entity.getWarningReason(),
                entity.getCreatedAt()
        );
    }

    /**
     * GroupWarningRow를 GroupWarningResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param row MyBatis 조회 결과 그룹 경고 Row
     * @return 변환이 완료된 GroupWarningResponse DTO
     */
    public static GroupWarningResponse from(GroupWarningRow row) {
        return new GroupWarningResponse(
                row.getGroupWarningId(),
                row.getGroupId(),
                row.getAdminId(),
                row.getAdminName(),
                row.getWarningReason(),
                row.getCreatedAt()
        );
    }
}
