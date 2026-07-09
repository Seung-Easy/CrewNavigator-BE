package seungeasy.crewnavigator.domain.post.repository;

<<<<<<< HEAD
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
import java.util.List;
>>>>>>> 4fcc35de4764cbdb3747f46c6a2d5bb3bbf4a70b
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
 * 2026.07.07: Chi-Yoon: 카테고리 ID 및 제목 키워드 다중 조건 검색을 위한 동적 JPQL 쿼리 메서드(searchPosts) 추가
<<<<<<< HEAD
 * 2026.07.09: Chi-Yoon: 공통 규격 벤치마킹을 위한 페이징 처리(Pageable, Page) 기능 도입 (💡 추가)
 * </pre>
 *
 * @author Seung-Geon, Chi-Yoon
 * @version 1.2
=======
 * </pre>
 *
 * @author Seung-Geon, Chi-Yoon
 * @version 1.1
>>>>>>> 4fcc35de4764cbdb3747f46c6a2d5bb3bbf4a70b
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
<<<<<<< HEAD
     * 카테고리 식별 번호와 제목 키워드를 기반으로 게시글 목록을 페이징하여 다중 조건 검색합니다.
     * 각 조건 필드가 null 이거나 빈 값일 경우 해당 필터링을 생략하는 동적 쿼리 방식으로 작동합니다.
     * 정렬 조건은 파라미터로 전달되는 Pageable 내부의 Sort 객체에 의해 동적으로 제어됩니다.
     *
     * @param categoryId 검색할 카테고리 식별 번호 (null 일 경우 카테고리 필터 생략)
     * @param title      검색할 제목 키워드 (null 이거나 빈 문자열일 경우 제목 필터 생략, 포함 검색 수행)
     * @param pageable   페이징 및 정렬 정보 (page, size, sort)
     * @return 다중 조건 필터링 및 페이징 처리가 완료된 게시글 엔티티 묶음 (Page)
     */
    @Query("SELECT p FROM Post p " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:title IS NULL OR :title = '' OR p.title LIKE CONCAT('%', :title, '%'))")
    Page<Post> searchPosts(@Param("categoryId") Long categoryId, @Param("title") String title, Pageable pageable);
=======
     * 카테고리 식별 번호와 제목 키워드를 기반으로 게시글 목록을 다중 조건 검색합니다.
     * 각 조건 필드가 null 이거나 빈 값일 경우 해당 필터링을 생략하는 동적 쿼리 방식으로 작동합니다.
     *
     * @param categoryId 검색할 카테고리 식별 번호 (null 일 경우 카테고리 필터 생략)
     * @param title      검색할 제목 키워드 (null 이거나 빈 문자열일 경우 제목 필터 생략, 포함 검색 수행)
     * @return 다중 조건 필터링을 통과한 게시글 엔티티 리스트
     */
    @Query("SELECT p FROM Post p " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:title IS NULL OR :title = '' OR p.title LIKE CONCAT('%', :title, '%')) " +
            "ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("categoryId") Long categoryId, @Param("title") String title);
>>>>>>> 4fcc35de4764cbdb3747f46c6a2d5bb3bbf4a70b
}