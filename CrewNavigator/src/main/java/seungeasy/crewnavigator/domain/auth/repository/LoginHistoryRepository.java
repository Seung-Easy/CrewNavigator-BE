package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.auth.entity.LoginHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 *  Interface Name: LoginHistoryRepository
 *  Description: 로그인 이력(LoginHistory) 엔티티에 대한 데이터 접근을 제공하는 리포지토리.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserIdOrderByLoginAtDesc(String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM LoginHistory lh WHERE lh.loginAt < :cutoff")
    void deleteByLoginAtBefore(LocalDateTime cutoff);
}
