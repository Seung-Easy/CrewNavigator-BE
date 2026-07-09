package seungeasy.crewnavigator.domain.comment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import seungeasy.crewnavigator.domain.comment.entity.Comment;

/**
 * <pre>
 * Interface Name: CommentRepository
 * Description: 댓글(Comment) 엔티티에 대한 데이터베이스 접근 기능을 제공하는 레포지토리 인터페이스.
 *
 * History
 * 2026.07.05: Chi-Yoon: 기본 CRUD 기능을 위한 JpaRepository 상속 및 최초 생성
 * 2026.07.07: Chi-Yoon: 게시글 번호(postId) 및 유저 아이디(userId) 다중 조건 검색을 위한 동적 JPQL 쿼리 메서드(searchComments) 추가
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글 식별 번호와 사용자 아이디를 기반으로 댓글 목록을 다중 조건 동적 검색합니다.
     * 각 조건 필드가 null 일 경우 해당 필터링 조건을 생략하고 데이터를 조회합니다.
     *
     * @param postId 검색할 게시글 식별 번호 (null 일 경우 게시글 필터 생략)
     * @param userId 검색할 사용자 아이디 (null 일 경우 작성자 필터 생략)
     * @return 다중 조건 필터링을 통과한 댓글 엔티티 리스트 (등록순 정렬)
     */
    @Query("SELECT c FROM Comment c " +
            "WHERE (:postId IS NULL OR c.post.id = :postId) " +
            "AND (:userId IS NULL OR c.writer.userId = :userId) " +
            "ORDER BY c.createdAt ASC")
    List<Comment> searchComments(@Param("postId") Long postId, @Param("userId") String userId);
}