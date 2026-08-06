package seungeasy.crewnavigator.domain.group.dto.request;

import java.util.List;

/**
 * <pre>
 * Class Name: GroupCreateRequestDto
 * Description: 그룹 생성 요청을 담는 Request DTO.
 *
 *  [필드]
 *  - groupName: 그룹명 (필수)
 *  - description: 그룹 소개
 *  - maxMembers: 그룹 정원 (기본 10)
 *  - isPrivate: 비공개 여부 ("Y"/"N", 기본 "N", "Y"면 그룹 검색에서 제외)
 *  - groupImage: 그룹 이미지 경로
 *  - tags: 그룹 태그 이름 목록
 *
 * History
 * 2026.08.02: Seung-Geon: 스텁 DTO를 그룹 도메인 확장에 맞춰 구현
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record GroupCreateRequestDto(
        String groupName,
        String description,
        Long maxMembers,
        String isPrivate,
        String groupImage,
        List<String> tags
) {
}
