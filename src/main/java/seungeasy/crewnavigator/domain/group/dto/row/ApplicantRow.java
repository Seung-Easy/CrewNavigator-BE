package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: ApplicantRow
 *  Description: MyBatis 매핑용 그룹 가입 신청자 Row DTO.
 *  ApplicantResponse로 변환되기 전 MyBatis 조회 결과(신청자 + 회원 JOIN)를 담는 중간 객체입니다.
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
public class ApplicantRow {

    /** 멤버 매핑 번호 (승인/거절 API에 사용) */
    private Long groupMemberId;

    /** 신청자 회원 아이디 */
    private String userId;

    /** 신청자 이름 */
    private String name;

    /** 신청 일시 */
    private LocalDateTime appliedAt;
}
