package seungeasy.crewnavigator.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seungeasy.crewnavigator.domain.auth.entity.User;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: Post
 * Description: 게시글 정보를 저장하는 엔티티.
 * 작성자(User) 및 카테고리(Category)와의 다대일 연관관계를 포함합니다.
 *
 * History
 * 2026.06.27: Chi-Yoon: DB 스키마 기반 엔티티 클래스 생성
 * 2026.06.27: Chi-Yoon: 빌더 패턴에 Category 매핑 누락 오류 수정
 * </pre>
 *
 * @author Seung-Geon, Chi-Yoon
 * @version 1.1
 */
@Entity
@Table(name = "`post`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_deleted", length = 1)
    private String isDeleted = "N";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        if (this.isDeleted == null) {
            this.isDeleted = "N";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 💡 수정된 빌더: Category를 파라미터로 받도록 변경!
    @Builder
    public Post(String title, String content, User writer, Category category) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.category = category;
        this.viewCount = 0;
        this.isDeleted = "N";
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void delete() {
        this.isDeleted = "Y";
    }
}