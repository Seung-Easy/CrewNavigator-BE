package seungeasy.crewnavigator.domain.comment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.post.entity.Post;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: Comment
 * Description: 댓글 정보를 저장하는 엔티티.
 * 게시글(Post) 및 작성자(User)와의 다대일 연관관계를 가집니다.
 *
 * History
 * 2026.07.05: Chi-Yoon: DB 스키마 기반 댓글 엔티티 클래스 최초 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Entity
@Table(name = "`comment`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_deleted", length = 1)
    private String isDeleted = "N";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 어떤 게시글에 달린 댓글인지 연관관계 설정 (N:1 단방향)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 댓글을 작성한 회원이 누구인지 연관관계 설정 (N:1 단방향)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isDeleted == null) {
            this.isDeleted = "N";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 아까 Post 빌더의 실수를 반복하지 않도록, 연관 관계 필드(post, writer)까지 확실하게 빌더에 포함!
    @Builder
    public Comment(String content, Post post, User writer) {
        this.content = content;
        this.post = post;
        this.writer = writer;
        this.isDeleted = "N";
    }


    public void update(String content) {
        this.content = content;
    }


    public void delete() {
        this.isDeleted = "Y";
    }
}