package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.auth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    Optional<User> findByNameAndEmail(String name, String email);
}
