package seungeasy.crewnavigator.domain.group.dto.request;

/**
 * <pre>
 * Class Name: GroupMemberRemoveRequestDto
 * Description: 그룹원 추방 요청을 담는 Request DTO.
 *
 *  [필드]
 *  - userId: 추방할 회원 아이디
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹원 추방 기능 구현을 위한 DTO 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupMemberRemoveRequestDto(
        String userId
) {
}
