package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.ApplicantRow;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApplicantResponse
 * Description: 그룹 가입 신청자(승인 대기자) 정보를 반환하기 위한 Response DTO.
 *
 * History
 * 2026.08.02: Seung-Geon: 스텁 DTO를 그룹 도메인 확장에 맞춰 구현
 * 2026.08.02: Seung-Geon: MyBatis 전환(CQRS)에 맞춰 팩토리를 ApplicantRow 기반으로 변경
 * 2026.08.13: Seung-Geon: 이전 나간 사유(leaveReason/leftAt) 필드 추가
 * 2026.08.29: Seung-Geon: 신청자 생일/성별 필드 추가
 * </pre>
 *
 * @param groupMemberId 멤버 매핑 번호 (승인/거절 API에 사용)
 * @param userId        신청자 회원 아이디
 * @param name          신청자 이름
 * @param birthday      신청자 생일
 * @param gender        신청자 성별 (M/F/N)
 * @param appliedAt     신청 일시
 * @param leaveReason   이전 나간 사유 (퇴장 후 재신청한 경우에만 값 존재)
 * @param leftAt        이전 나간 일시 (퇴장 후 재신청한 경우에만 값 존재)
 * @author Seung-Geon
 * @version 1.3
 */
public record ApplicantResponse(
        Long groupMemberId,
        String userId,
        String name,
        LocalDate birthday,
        String gender,
        LocalDateTime appliedAt,
        String leaveReason,
        LocalDateTime leftAt
) {
    /**
     * ApplicantRow를 ApplicantResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param row MyBatis 조회 결과 신청자 Row (회원 정보 JOIN 포함)
     * @return 변환이 완료된 ApplicantResponse DTO
     */
    public static ApplicantResponse from(ApplicantRow row) {
        return new ApplicantResponse(
                row.getGroupMemberId(),
                row.getUserId(),
                row.getName(),
                row.getBirthday(),
                row.getGender(),
                row.getAppliedAt(),
                row.getLeaveReason(),
                row.getLeftAt()
        );
    }
}
