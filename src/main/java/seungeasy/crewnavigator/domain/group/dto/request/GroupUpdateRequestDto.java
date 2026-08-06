package seungeasy.crewnavigator.domain.group.dto.request;

import java.util.List;

/**
 * <pre>
 * Class Name: GroupUpdateRequestDto
 * Description: 그룹 정보 수정 요청을 담는 Request DTO.
 *
 *  [필드]
 *  - groupName: 수정할 그룹명 (null이면 유지)
 *  - description: 수정할 그룹 소개 (null이면 유지)
 *  - maxMembers: 수정할 그룹 정원 (null이면 유지)
 *  - isPrivate: 수정할 비공개 여부 "Y"/"N" (null이면 유지, "Y"면 그룹 검색에서 제외)
 *  - groupImage: 수정할 그룹 이미지 경로 (null이면 유지)
 *  - tags: 교체할 태그 목록 (null이면 기존 태그 유지)
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 정보 수정 API(누락 API) 구현을 위한 DTO 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupUpdateRequestDto(
        String groupName,
        String description,
        Long maxMembers,
        String isPrivate,
        String groupImage,
        List<String> tags
) {
}
