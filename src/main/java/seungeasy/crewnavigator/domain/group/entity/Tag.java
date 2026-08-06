package seungeasy.crewnavigator.domain.group.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 *  Class Name: Tag
 *  Description: 그룹 태그 정보를 저장하는 엔티티.
 *
 *  [주요 필드]
 *  - tagId: 태그 번호 (PK, AUTO_INCREMENT)
 *  - tagName: 태그 이름 (NOT NULL, UNIQUE)
 *  - tagDescription: 태그 설명
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name", length = 50, nullable = false, unique = true)
    private String tagName;

    @Column(name = "tag_description", length = 255)
    private String tagDescription;
}
