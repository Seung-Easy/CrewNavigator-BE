package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
public class LoginHistory {

    @Id
    @Column(name = "login_history_id")
    private Long loginHistoryId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "login_at")
    private LocalDateTime loginAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "is_activited")
    private Boolean isActivated;

    @PrePersist
    protected void onCreate() {
        if (this.loginAt == null) {
            this.loginAt = LocalDateTime.now();
        }
    }
}
