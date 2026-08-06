package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.ApplicantRow;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApplicantResponse
 * Description: 그룹 가입 신청자(승인 대기자) 정보를 반환하기 위한 Response DTO.
 *
 * History
 * 2026.08.02: Seung-Geon: 스텁 DTO를 그룹 도메인 확장에 맞춰 구현
 * 2026.08.02: Seung-Geon: MyBatis 전환(CQRS)에 맞춰 팩토리를 ApplicantRow 기반으로 변경
 * </pre>
 *
 * @param groupMemberId 멤버 매핑 번호 (승인/거절 API에 사용)
 * @param userId        신청자 회원 아이디
 * @param name          신청자 이름
 * @param appliedAt     신청 일시
 * @author Seung-Geon
 * @version 1.1
 */
public record ApplicantResponse(
        Long groupMemberId,
        String userId,
        String name,
        LocalDateTime appliedAt
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
                row.getAppliedAt()
        );
    }
}
