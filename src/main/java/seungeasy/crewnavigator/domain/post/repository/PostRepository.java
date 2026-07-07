package seungeasy.crewnavigator.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seungeasy.crewnavigator.domain.post.entity.Post;

/**
 * <pre>
 * Interface Name: PostRepository
 * Description: Post 엔티티에 대한 데이터베이스 액세스 처리를 담당하는 레포지토리.
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 메서드를 자동으로 제공받습니다.
 *
 * History
 * 2026.06.27: Seung-Geon: 레포지토리 인터페이스 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}