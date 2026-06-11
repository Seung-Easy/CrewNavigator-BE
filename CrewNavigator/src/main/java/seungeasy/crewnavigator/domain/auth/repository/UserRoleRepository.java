package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.auth.entity.UserRole;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    List<UserRole> findByIdUserId(String userId);
}
