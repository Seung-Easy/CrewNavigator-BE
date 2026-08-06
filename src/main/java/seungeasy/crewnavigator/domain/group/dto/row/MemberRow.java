package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: MemberRow
 *  Description: MyBatis 매핑용 그룹 멤버 Row DTO.
 *  MemberResponse로 변환되기 전 MyBatis 조회 결과(멤버 + 회원 JOIN)를 담는 중간 객체입니다.
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 조회 MyBatis 전환(CQRS)을 위해 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class MemberRow {

    /** 멤버 회원 아이디 */
    private String userId;

    /** 멤버 이름 */
    private String name;

    /** 멤버 프로필 이미지 경로 */
    private String userImage;

    /** 그룹 내 권한 (LEADER, MEMBER) */
    private String memberRole;

    /** 가입 확정 일시 (승인/수락 처리 일시) */
    private LocalDateTime joinedAt;
}
