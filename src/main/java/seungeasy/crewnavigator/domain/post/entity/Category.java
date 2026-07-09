package seungeasy.crewnavigator.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: Category
 * Description: 게시글 카테고리 정보 테이블과 매핑되는 엔티티 클래스.
 *
 * History
 * 2026.06.27: Chi-Yoon: post 패키지 내부에 카테고리 엔티티 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @Column(name = "category_description", length = 255)
    private String categoryDescription;
}