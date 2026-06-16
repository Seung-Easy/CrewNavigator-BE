package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 *  Interface Name: UserRepository
 *  Description: 사용자(User) 엔티티에 대한 데이터 접근을 제공하는 리포지토리.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.16: Seung-Geon: searchUsers, countByStatus, countTodaySignups, totalSignups 쿼리 메서드 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    Optional<User> findByNameAndEmail(String name, String email);

    List<User> findAllByStatusAndDeletedAtBefore(UserStatus status, LocalDateTime deletedAt);

    /**
     * 관리자용 회원 검색 (페이징 + 필터)
     * keyword: userId, name, email을 대상으로 LIKE 검색
     * status: null이면 전체, 특정 값이면 해당 상태만 조회
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:keyword IS NULL OR u.userId LIKE %:keyword% OR u.name LIKE %:keyword% OR u.email LIKE %:keyword%)")
    Page<User> searchUsers(@Param("status") UserStatus status,
                           @Param("keyword") String keyword,
                           Pageable pageable);

    /** 상태별 회원 수 */
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countByStatus();

    /** 오늘 가입한 회원 수 */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startOfDay")
    long countTodaySignups(@Param("startOfDay") LocalDateTime startOfDay);

    /** 전체 가입자 수 (LEAVE 포함) */
    @Query("SELECT COUNT(u) FROM User u")
    long totalSignups();
}
