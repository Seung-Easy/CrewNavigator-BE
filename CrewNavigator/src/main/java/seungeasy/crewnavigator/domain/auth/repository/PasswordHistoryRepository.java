package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.auth.entity.PasswordHistory;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findByUserIdOrderByChangedAtDesc(String userId);
}
