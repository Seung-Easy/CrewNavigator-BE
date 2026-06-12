package seungeasy.crewnavigator.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.auth.entity.UserRole;

import java.util.List;

/**
 * <pre>
 *  Interface Name: UserRoleRepository
 *  Description: 사용자-권한 매핑(UserRole) 엔티티에 대한 데이터 접근을 제공하는 리포지토리.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    List<UserRole> findByIdUserId(String userId);
}
