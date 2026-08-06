package seungeasy.crewnavigator.domain.group.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seungeasy.crewnavigator.domain.group.type.GroupMemberRole;
import seungeasy.crewnavigator.domain.group.type.JoinStatus;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: GroupMember
 *  Description: 그룹과 회원의 가입 관계를 저장하는 엔티티.
 *
 *  [주요 필드]
 *  - groupMemberId: 멤버 매핑 번호 (PK, AUTO_INCREMENT)
 *  - groupId: 그룹 번호 (FK)
 *  - userId: 회원 아이디 (FK)
 *  - memberRole: 그룹 내 권한 (LEADER, MEMBER)
 *  - joinStatus: 가입 상태 (PENDING, APPROVED, REJECTED, INVITED)
 *  - appliedAt: 신청 일시
 *  - processedAt: 처리 일시 (승인/거절/수락 확정 시점)
 *
 *  [비즈니스 규칙]
 *  - (groupId, userId) 조합은 UNIQUE — 같은 그룹에 중복 가입/신청 불가
 *
 * History
 * 2026.07.31: Seung-Geon: 스텁 클래스를 그룹 도메인 확장에 맞춰 구현
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(
        name = "group_member",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_group_user",
                columnNames = {"group_id", "user_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long groupMemberId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", length = 20)
    private GroupMemberRole memberRole = GroupMemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_status", length = 20)
    private JoinStatus joinStatus = JoinStatus.PENDING;

    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * 엔티티 최초 저장 전 실행됩니다. appliedAt과 기본값을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        if (this.memberRole == null) {
            this.memberRole = GroupMemberRole.MEMBER;
        }
        if (this.joinStatus == null) {
            this.joinStatus = JoinStatus.PENDING;
        }
    }

    /**
     * 가입 신청(PENDING) 상태의 GroupMember를 생성합니다.
     * <p>
     * 그룹 가입 신청 시 생성되는 기본 상태로, 그룹장의 승인을 기다립니다.
     * (is_private는 그룹 검색 노출 여부를 나타내는 설정이며 가입 승인 방식과 무관합니다)
     *
     * @param groupId 대상 그룹 번호
     * @param userId  신청자 회원 아이디
     * @return PENDING 상태의 GroupMember
     */
    public static GroupMember createPending(Long groupId, String userId) {
        GroupMember member = new GroupMember();
        member.groupId = groupId;
        member.userId = userId;
        member.memberRole = GroupMemberRole.MEMBER;
        member.joinStatus = JoinStatus.PENDING;
        return member;
    }

    /**
     * 초대 대기(INVITED) 상태의 GroupMember를 생성합니다.
     *
     * @param groupId 대상 그룹 번호
     * @param userId  초대받은 회원 아이디
     * @return INVITED 상태의 GroupMember
     */
    public static GroupMember createInvited(Long groupId, String userId) {
        GroupMember member = new GroupMember();
        member.groupId = groupId;
        member.userId = userId;
        member.memberRole = GroupMemberRole.MEMBER;
        member.joinStatus = JoinStatus.INVITED;
        return member;
    }

    /**
     * 거절(REJECTED)된 가입 신청을 다시 신청(PENDING) 상태로 전환합니다.
     * <p>
     * (groupId, userId) UNIQUE 제약 때문에 거절된 행을 재사용하여 재신청을 지원합니다.
     * 처리 일시를 초기화하고 신청 일시를 현재 시각으로 갱신합니다.
     */
    public void reapply() {
        this.joinStatus = JoinStatus.PENDING;
        this.processedAt = null;
        this.appliedAt = LocalDateTime.now();
    }

    /**
     * 가입 신청을 승인 처리합니다. 상태를 APPROVED로 변경하고 처리 일시를 기록합니다.
     */
    public void approve() {
        this.joinStatus = JoinStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 가입 신청을 거절 처리합니다. 상태를 REJECTED로 변경하고 처리 일시를 기록합니다.
     */
    public void reject() {
        this.joinStatus = JoinStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 그룹장 초대를 수락 처리합니다. 상태를 APPROVED로 변경하고 처리 일시를 기록합니다.
     */
    public void acceptInvitation() {
        this.joinStatus = JoinStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 그룹장 초대를 거절 처리합니다. 상태를 REJECTED로 변경하고 처리 일시를 기록합니다.
     */
    public void declineInvitation() {
        this.joinStatus = JoinStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 멤버 권한을 변경합니다.
     *
     * @param role 변경할 권한 (LEADER, MEMBER)
     */
    public void changeRole(GroupMemberRole role) {
        this.memberRole = role;
    }
}
