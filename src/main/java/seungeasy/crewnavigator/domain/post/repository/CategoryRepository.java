package seungeasy.crewnavigator.domain.post.repository; // post 패키지 안으로 지정!

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.post.entity.Category; // post.entity 안의 Category 임포트

/**
 * <pre>
 * Interface Name: CategoryRepository
 * Description: 게시글 카테고리(Category) 엔티티에 대한 데이터 접근을 제공하는 리포지토리.
 *
 * History
 * 2026.06.27: Chi-Yoon: post 패키지 내부에 최초 생성 및 JpaRepository 상속
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}