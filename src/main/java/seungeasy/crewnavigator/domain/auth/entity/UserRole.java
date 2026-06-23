package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

/**
 * <pre>
 *  Class Name: UserRole
 *  Description: 사용자와 역할(Role) 간의 다대다 관계를 표현하는 매핑 엔티티.
 *  복합키(UserRoleId)를 사용하여 user_id와 role_id를 연결합니다.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: Role @ManyToOne 관계 추가 (권한 조회를 위한 연관관계)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    public UserRole(UserRoleId id) {
        this.id = id;
    }

    public UserRole(Long roleId, String userId) {
        this.id = new UserRoleId(roleId, userId);
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRoleId implements Serializable {

        @Column(name = "role_id")
        private Long roleId;

        @Column(name = "user_id", length = 50)
        private String userId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserRoleId that = (UserRoleId) o;
            return Objects.equals(roleId, that.roleId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roleId, userId);
        }
    }
}
