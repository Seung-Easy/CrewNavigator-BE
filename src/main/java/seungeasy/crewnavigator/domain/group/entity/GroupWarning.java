package seungeasy.crewnavigator.domain.group.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: GroupWarning
 *  Description: 그룹 경고 정보를 저장하는 엔티티.
 *
 *  [주요 필드]
 *  - groupWarningId: 그룹 경고 번호 (PK, AUTO_INCREMENT)
 *  - groupId: 그룹 번호 (FK)
 *  - adminId: 경고를 부여한 관리자 아이디
 *  - warningReason: 경고 사유
 *  - createdAt: 경고 일시
 *
 *  [비즈니스 규칙]
 *  - 어드민이 문제 있는 그룹에 경고를 부여합니다.
 *  - 경고 누적 자동 처리는 없으며 수동으로만 처리합니다.
 *  - 그룹 소프트 삭제 시 경고 기록도 함께 삭제합니다.
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 엔티티 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(name = "group_warning")
@Getter
@Setter
@NoArgsConstructor
public class GroupWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_warning_id")
    private Long groupWarningId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "admin_id", length = 50, nullable = false)
    private String adminId;

    @Column(name = "warning_reason", columnDefinition = "TEXT", nullable = false)
    private String warningReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 그룹 경고 엔티티를 생성하는 정적 팩토리 메서드입니다.
     *
     * @param groupId       그룹 번호
     * @param adminId       관리자 아이디
     * @param warningReason 경고 사유
     * @return 생성된 GroupWarning 엔티티
     */
    public static GroupWarning create(Long groupId, String adminId, String warningReason) {
        GroupWarning warning = new GroupWarning();
        warning.setGroupId(groupId);
        warning.setAdminId(adminId);
        warning.setWarningReason(warningReason);
        warning.setCreatedAt(LocalDateTime.now());
        return warning;
    }
}
