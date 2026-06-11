package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_history")
@Getter
@Setter
@NoArgsConstructor
public class PasswordHistory {

    @Id
    @Column(name = "password_history_id")
    private Long passwordHistoryId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "Before_changed_pwd", length = 255)
    private String beforeChangedPwd;

    @PrePersist
    protected void onCreate() {
        if (this.changedAt == null) {
            this.changedAt = LocalDateTime.now();
        }
    }
}
