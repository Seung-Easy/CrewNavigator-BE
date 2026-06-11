package seungeasy.crewnavigator.domain.auth.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

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
