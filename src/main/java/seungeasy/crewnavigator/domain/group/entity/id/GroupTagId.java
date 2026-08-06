package seungeasy.crewnavigator.domain.group.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * <pre>
 *  Class Name: GroupTagId
 *  Description: 그룹-태그 매핑의 복합 키 클래스 (@Embeddable).
 *
 *  [주요 필드]
 *  - groupId: 그룹 번호 (FK group_id)
 *  - tagId: 태그 번호 (FK tag_id)
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Embeddable // 이 객체가 다른 엔티티에 삽입될 수 있음을 알립니다.
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class GroupTagId implements Serializable {

    /** 그룹 번호 (FK group_id) */
    private Long groupId;

    /** 태그 번호 (FK tag_id) */
    private Long tagId;
}
