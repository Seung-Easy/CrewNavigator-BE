package seungeasy.crewnavigator.domain.group.dto.request;

/**
 * <pre>
 * Class Name: GroupMemberRemoveRequestDto
 * Description: 그룹원 추방 요청을 담는 Request DTO.
 *
 *  [필드]
 *  - userId: 추방할 회원 아이디
 *  - leaveReason: 추방 사유 (선택 — LEFT 상태 기록용)
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹원 추방 기능 구현을 위한 DTO 생성
 * 2026.08.13: Seung-Geon: 추방 사유(leaveReason) 필드 추가 (soft-delete 전환)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public record GroupMemberRemoveRequestDto(
        String userId,
        String leaveReason
) {
}
