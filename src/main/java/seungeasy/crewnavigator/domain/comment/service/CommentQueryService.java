package seungeasy.crewnavigator.domain.comment.service;

import org.springframework.data.domain.Page;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentSearchRequest;
import seungeasy.crewnavigator.domain.comment.dto.response.CommentResponse;

/**
 * <pre>
 * Interface Name: CommentQueryService
 * Description: 댓글(Comment) 관련 읽기/조회(Query) 작업을 처리하는 서비스 인터페이스.
 *
 * History
 * 2026.07.07: Chi-Yoon: 댓글 다중 조건 검색을 위한 조회 메서드(searchComments) 정의
 * 2026.07.09: Chi-Yoon: 공통 규격 벤치마킹을 위한 페이징 처리(Page 반환 및 page/size 파라미터) 반영
 * 2026.07.21: Chi-Yoon: 댓글 단건 상세 조회를 위한 메서드(getComment) 정의 (💡 추가)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.2
 */
public interface CommentQueryService {

    /**
     * 게시글 번호 및 사용자 아이디를 조건으로 하여 필터링된 댓글 목록을 페이징하여 조회합니다.
     *
     * @param request 검색 조건 파라미터가 담긴 Request DTO
     * @param page    조회할 페이지 번호 (0부터 시작)
     * @param size    한 페이지당 보여줄 댓글 개수
     * @return 검색 조건에 부합하고 페이징 처리가 완료된 댓글 응답 DTO 묶음 (Page)
     */
    Page<CommentResponse> searchComments(CommentSearchRequest request, int page, int size);

    /**
     * 특정 댓글 식별 번호(ID)를 기반으로 단건 상세 정보를 조회합니다.
     *
     * @param commentId 조회할 댓글 식별 번호
     * @return 댓글 상세 정보를 담은 응답 DTO
     */
    CommentResponse getComment(Long commentId);
}