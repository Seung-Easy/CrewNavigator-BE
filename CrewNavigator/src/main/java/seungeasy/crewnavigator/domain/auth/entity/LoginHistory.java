package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: LoginHistory
 *  Description: 사용자 로그인 시도 이력을 저장하는 엔티티.
 *  성공/실패 여부와 IP 주소를 기록하여 감사(Audit) 목적으로 사용됩니다.
 *  90일 초과 데이터는 LoginHistoryCleanupScheduler에 의해 정리됩니다.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: @GeneratedValue 누락 버그 수정
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
