package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: PasswordHistory
 *  Description: 사용자 비밀번호 변경 이력을 저장하는 엔티티.
 *  변경 전 비밀번호를 보관하여 재사용을 방지하는 용도로 사용됩니다.
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
@Table(name = "password_history")
@Getter
@Setter
@NoArgsConstructor
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
