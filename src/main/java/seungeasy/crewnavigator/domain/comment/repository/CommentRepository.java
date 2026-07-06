package seungeasy.crewnavigator.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seungeasy.crewnavigator.domain.comment.entity.Comment;

/**
 * <pre>
 * Interface Name: CommentRepository
 * Description: 댓글(Comment) 엔티티에 대한 데이터베이스 접근 기능을 제공하는 레포지토리 인터페이스.
 *
 * History
 * 2026.07.05: Chi-Yoon: 기본 CRUD 기능을 위한 JpaRepository 상속 및 최초 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 기본 상속 메서드(save, findById, delete 등) 외에
    // 나중에 특정 게시글의 댓글 목록을 조회하는 쿼리 메서드 등이 필요할 때 이곳에 추가 확장합니다.
}