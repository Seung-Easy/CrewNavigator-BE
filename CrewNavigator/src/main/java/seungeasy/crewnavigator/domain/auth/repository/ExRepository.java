package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.auth.entity.ExEntity;

public interface ExRepository extends JpaRepository<ExEntity, Integer> {
}
