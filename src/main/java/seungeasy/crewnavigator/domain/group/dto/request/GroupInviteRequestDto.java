package seungeasy.crewnavigator.domain.group.dto.request;

import java.util.List;

/**
 * <pre>
 * Class Name: GroupInviteRequestDto
 * Description: 그룹원 초대 요청을 담는 Request DTO.
 *
 *  [필드]
 *  - userIds: 초대할 회원 아이디 목록
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹원 초대 기능 구현을 위한 DTO 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupInviteRequestDto(
        List<String> userIds
) {
}
