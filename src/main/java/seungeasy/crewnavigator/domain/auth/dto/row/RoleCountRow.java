package seungeasy.crewnavigator.domain.auth.dto.row;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 *  Class Name: RoleCountRow
 *  Description: MyBatis 매핑용 권한별 회원 수 Row DTO.
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class RoleCountRow {

    private String roleName;
    private long count;
}
