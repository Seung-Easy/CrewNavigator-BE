package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAccountNonLocked() {
        return !"Y".equals(this.isLocked);
    }

    public boolean isAccountActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
