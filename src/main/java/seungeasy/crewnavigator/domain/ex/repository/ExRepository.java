package seungeasy.crewnavigator.domain.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.ex.entity.ExEntity;

public interface ExRepository extends JpaRepository<ExEntity, Integer> {
}
