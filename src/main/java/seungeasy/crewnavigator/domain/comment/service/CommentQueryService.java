package seungeasy.crewnavigator.domain.comment.service;

import java.util.List;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentSearchRequest;
import seungeasy.crewnavigator.domain.comment.dto.response.CommentResponse;

/**
 * <pre>
 * Interface Name: CommentQueryService
 * Description: 댓글(Comment) 관련 읽기/조회(Query) 작업을 처리하는 서비스 인터페이스.
 *
 * History
 * 2026.07.07: Chi-Yoon: 댓글 다중 조건 검색을 위한 조회 메서드(searchComments) 정의
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
public interface CommentQueryService {
    List<CommentResponse> searchComments(CommentSearchRequest request);
}