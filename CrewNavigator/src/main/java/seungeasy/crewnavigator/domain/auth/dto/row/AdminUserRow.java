package seungeasy.crewnavigator.domain.auth.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: AdminUserRow
 *  Description: MyBatis 매핑용 회원 Row DTO.
 *  AdminUserResponse로 변환되기 전 MyBatis 결과를 담는 중간 객체입니다.
 *  roles는 GROUP_CONCAT 결과를 문자열로 받아 service에서 List로 변환합니다.
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
public class AdminUserRow {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String status;
    private String isLocked;
    private String roles;
    private String userImage;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
