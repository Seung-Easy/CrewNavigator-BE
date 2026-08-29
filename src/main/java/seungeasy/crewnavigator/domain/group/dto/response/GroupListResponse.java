package seungeasy.crewnavigator.domain.group.dto.response;

import seungeasy.crewnavigator.domain.group.dto.row.GroupRow;

import java.util.List;

/**
 * <pre>
 * Class Name: GroupListResponse
 * Description: 그룹 목록 조회 결과를 클라이언트에게 반환하기 위한 Response DTO. (생성/수정일시 제외)
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 목록 조회용 간소화 DTO 생성 (createdAt/updatedAt 제외)
 * </pre>
 *
 * @param groupId     그룹 번호 (PK)
 * @param leaderId    그룹장 회원 아이디
 * @param groupName   그룹명
 * @param maxMembers  그룹 정원
 * @param memberCount 현재 가입 멤버 수 (APPROVED 기준)
 * @param description 그룹 소개
 * @param isPrivate   비공개 여부 ("Y"/"N", "Y"면 그룹 검색에서 제외)
 * @param groupImage  그룹 이미지 경로
 * @param tags        그룹 태그 이름 목록
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupListResponse(
        Long groupId,
        String leaderId,
        String groupName,
        Long maxMembers,
        Long memberCount,
        String description,
        String isPrivate,
        String groupImage,
        List<String> tags
) {
    /**
     * GroupRow를 GroupListResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     * 멤버 수는 서브쿼리(memberCount), 태그는 GROUP_CONCAT(tags) 결과를 List로 변환하여 전달합니다.
     *
     * @param row  MyBatis 조회 결과 그룹 Row (멤버 수, 태그 포함)
     * @param tags 그룹의 태그 이름 목록
     * @return 변환이 완료된 GroupListResponse DTO
     */
    public static GroupListResponse from(GroupRow row, List<String> tags) {
        return new GroupListResponse(
                row.getGroupId(),
                row.getLeaderId(),
                row.getGroupName(),
                row.getMaxMembers(),
                row.getMemberCount(),
                row.getDescription(),
                row.getIsPrivate(),
                row.getGroupImage(),
                tags
        );
    }
}
