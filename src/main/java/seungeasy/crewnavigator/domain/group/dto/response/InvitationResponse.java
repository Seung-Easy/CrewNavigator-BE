package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.InvitationRow;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: InvitationResponse
 * Description: 내가 초대받은 그룹(INVITED) 목록을 반환하기 위한 Response DTO.
 *
 * History
 * 2026.09.02: Seung-Geon: 내가 초대받은 그룹 목록 조회 기능을 위해 생성
 * </pre>
 *
 * @param invitationId 초대 매핑 번호 (수락/거절 API에 사용)
 * @param groupId      그룹 번호
 * @param groupName    그룹명
 * @param leaderId     그룹장 회원 아이디
 * @param leaderName   그룹장 이름
 * @param maxMembers   그룹 정원
 * @param description  그룹 소개
 * @param isPrivate    비공개 여부 ("Y"/"N")
 * @param groupImage   그룹 이미지 경로
 * @param memberCount  현재 가입 멤버 수
 * @param tags         그룹 태그 목록
 * @param invitedAt    초대 일시
 * @author Seung-Geon
 * @version 1.0
 */
public record InvitationResponse(
        Long invitationId,
        Long groupId,
        String groupName,
        String leaderId,
        String leaderName,
        Long maxMembers,
        String description,
        String isPrivate,
        String groupImage,
        Long memberCount,
        List<String> tags,
        LocalDateTime invitedAt
) {
    /**
     * InvitationRow를 InvitationResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param row    MyBatis 조회 결과 초대 Row
     * @param tags   GROUP_CONCAT으로 받은 태그 문자열을 분리한 목록
     * @return 변환이 완료된 InvitationResponse DTO
     */
    public static InvitationResponse from(InvitationRow row, List<String> tags) {
        return new InvitationResponse(
                row.getInvitationId(),
                row.getGroupId(),
                row.getGroupName(),
                row.getLeaderId(),
                row.getLeaderName(),
                row.getMaxMembers(),
                row.getDescription(),
                row.getIsPrivate(),
                row.getGroupImage(),
                row.getMemberCount(),
                tags,
                row.getInvitedAt()
        );
    }
}
