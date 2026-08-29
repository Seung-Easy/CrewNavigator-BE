package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: GroupWarningRow
 *  Description: MyBatis 매핑용 그룹 경고 Row DTO.
 *  GroupWarningResponse로 변환되기 전 MyBatis 조회 결과를 담는 중간 객체입니다.
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 Row DTO 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class GroupWarningRow {

    /** 그룹 경고 번호 */
    private Long groupWarningId;

    /** 그룹 번호 */
    private Long groupId;

    /** 경고를 부여한 관리자 아이디 */
    private String adminId;

    /** 경고 사유 */
    private String warningReason;

    /** 경고 일시 */
    private LocalDateTime createdAt;

    /** 경고를 부여한 관리자 이름 */
    private String adminName;
}
