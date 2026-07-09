package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: User
 *  Description: 사용자 계정 정보를 저장하는 엔티티.
 *  Spring Security의 UserDetails와 연동되어 인증/인가에 사용됩니다.
 *
 *  [주요 필드]
 *  - userId: 사용자 ID (PK)
 *  - loginFailCount, isLocked: 계정 잠금 관련 필드 (로그인 실패 횟수 추적)
 *  - status: 계정 상태 (ACTIVE / INACTIVE / LEAVE)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(name = "\u0060user\u0060")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "login_fail_count")
    private Integer loginFailCount = 0;

    @Column(name = "is_locked", length = 1)
    private String isLocked = "N";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "user_image", length = 255)
    private String userImage;

    /**
     * 엔티티 최초 저장 전 실행됩니다. createdAt과 기본값을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.loginFailCount == null) {
            this.loginFailCount = 0;
        }
        if (this.isLocked == null) {
            this.isLocked = "N";
        }
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }

    /**
     * 엔티티 업데이트 전 실행됩니다. updatedAt을 현재 시간으로 갱신합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 계정 잠금 여부를 반환합니다. isLocked가 "Y"면 잠긴 계정으로 간주합니다.
     *
     * @return 계정이 잠기지 않았으면 true
     */
    public boolean isAccountNonLocked() {
        return !"Y".equals(this.isLocked);
    }

    /**
     * 계정 활성화 여부를 반환합니다. status가 ACTIVE인 경우만 활성으로 간주합니다.
     *
     * @return 계정이 활성 상태이면 true
     */
    public boolean isAccountActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
