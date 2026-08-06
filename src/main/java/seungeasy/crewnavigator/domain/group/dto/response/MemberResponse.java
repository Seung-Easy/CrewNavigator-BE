package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.MemberRow;
import seungeasy.crewnavigator.domain.group.type.GroupMemberRole;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: MemberResponse
 * Description: 그룹에 속한 멤버 정보를 반환하기 위한 Response DTO.
 *
 * History
 * 2026.08.02: Seung-Geon: 스텁 DTO를 그룹 도메인 확장에 맞춰 구현
 * 2026.08.02: Seung-Geon: MyBatis 전환(CQRS)에 맞춰 팩토리를 MemberRow 기반으로 변경
 * </pre>
 *
 * @param userId     멤버 회원 아이디
 * @param name       멤버 이름
 * @param userImage  멤버 프로필 이미지 경로
 * @param memberRole 그룹 내 권한 (LEADER, MEMBER)
 * @param joinedAt   가입 확정 일시 (승인/수락 처리 일시)
 * @author Seung-Geon
 * @version 1.1
 */
public record MemberResponse(
        String userId,
        String name,
        String userImage,
        GroupMemberRole memberRole,
        LocalDateTime joinedAt
) {
    /**
     * MemberRow를 MemberResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     * memberRole은 문자열(DB 값)을 GroupMemberRole enum으로 변환합니다.
     *
     * @param row MyBatis 조회 결과 멤버 Row (회원 정보 JOIN 포함)
     * @return 변환이 완료된 MemberResponse DTO
     */
    public static MemberResponse from(MemberRow row) {
        return new MemberResponse(
                row.getUserId(),
                row.getName(),
                row.getUserImage(),
                GroupMemberRole.valueOf(row.getMemberRole()),
                row.getJoinedAt()
        );
    }
}
