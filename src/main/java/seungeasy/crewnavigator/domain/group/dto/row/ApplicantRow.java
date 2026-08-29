package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: ApplicantRow
 *  Description: MyBatis 매핑용 그룹 가입 신청자 Row DTO.
 *  ApplicantResponse로 변환되기 전 MyBatis 조회 결과(신청자 + 회원 JOIN)를 담는 중간 객체입니다.
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 조회 MyBatis 전환(CQRS)을 위해 생성
 * 2026.08.13: Seung-Geon: 퇴장(LEFT) 이력 조회를 위해 leaveReason/leftAt 필드 추가
 * 2026.08.29: Seung-Geon: 신청자 생일/성별 필드 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.2
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

    /** 신청자 생일 */
    private LocalDate birthday;

    /** 신청자 성별 (M/F/N) */
    private String gender;

    /** 신청 일시 */
    private LocalDateTime appliedAt;

    /** 이전 나간 사유 (퇴장 후 재신청한 경우에만 값 존재) */
    private String leaveReason;

    /** 이전 나간 일시 (퇴장 후 재신청한 경우에만 값 존재) */
    private LocalDateTime leftAt;
}
