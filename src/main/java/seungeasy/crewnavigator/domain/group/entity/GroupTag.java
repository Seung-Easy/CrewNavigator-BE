package seungeasy.crewnavigator.domain.group.entity;


import jakarta.persistence.*;
import lombok.*;
import seungeasy.crewnavigator.domain.group.entity.id.GroupTagId;

import java.io.Serializable;

/**
 * <pre>
 *  Class Name: GroupTag
 *  Description: 그룹-태그 매핑을 저장하는 엔티티.
 *  복합 키(tag_id, group_id)를 @EmbeddedId로 사용합니다.
 *
 *  [주요 필드]
 *  - groupTagId: 복합 키 (groupId + tagId)
 *  - crewGroup: 매핑된 그룹 (FK group_id)
 *  - tag: 매핑된 태그 (FK tag_id)
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(name = "group_tag")
@Getter
@Setter
@NoArgsConstructor // JPA가 객체를 생성할 때 필요한 기본 생성자
@AllArgsConstructor // 편의를 위한 모든 필드 생성자
public class GroupTag {

    /**
     * 복합 키 (groupId + tagId) - @EmbeddedId로 사용
     */
    @EmbeddedId
    private GroupTagId groupTagId;

    /**
     * 매핑된 그룹 (GroupTagId.groupId에 매핑)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId") // GroupTagId의 groupId 필드에 매핑
    @JoinColumn(name = "group_id")
    private CrewGroup crewGroup;

    /**
     * 매핑된 태그 (GroupTagId.tagId에 매핑)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId") // GroupTagId의 tagId 필드에 매핑
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
