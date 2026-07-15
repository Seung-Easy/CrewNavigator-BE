package seungeasy.crewnavigator.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.user.entity.UserInfo;

/**
 * <pre>
 *  Interface Name: UserInfoRepository
 *  Description: 사용자(User) 엔티티에 대한 데이터 접근을 제공하는 리포지토리.
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

}
