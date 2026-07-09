package seungeasy.crewnavigator.domain.post.service;

<<<<<<< HEAD
import org.springframework.data.domain.Page;
=======
import java.util.List;
>>>>>>> 4fcc35de4764cbdb3747f46c6a2d5bb3bbf4a70b
import seungeasy.crewnavigator.domain.post.dto.request.PostSearchRequest;
import seungeasy.crewnavigator.domain.post.dto.response.PostResponse;

/**
 * <pre>
 * Interface Name: PostQueryService
 * Description: 게시글(Post) 관련 읽기/조회(Query) 작업을 처리하는 서비스 인터페이스.
 *
 * History
 * 2026.07.07: Chi-Yoon: 게시글 다중 조건 검색을 위한 조회 메서드(searchPosts) 정의
<<<<<<< HEAD
 * 2026.07.09: Chi-Yoon: 공통 규격 벤치마킹을 위한 페이징 처리(Page 반환 및 page/size 파라미터) 반영 (💡 추가)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
=======
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
>>>>>>> 4fcc35de4764cbdb3747f46c6a2d5bb3bbf4a70b
 */
public interface PostQueryService {

    /**
<<<<<<< HEAD
     * 카테고리 ID 및 제목 키워드를 조건으로 하여 필터링된 게시글 목록을 페이징하여 조회합니다.
     *
     * @param request 검색 조건 파라미터가 담긴 Request DTO
     * @param page    조회할 페이지 번호 (0부터 시작)
     * @param size    한 페이지당 보여줄 게시글 개수
     * @return 검색 조건에 부합하고 페이징 처리가 완료된 게시글 응답 DTO 묶음 (Page)
     */
    Page<PostResponse> searchPosts(PostSearchRequest request, int page, int size);
=======
     * 카테고리 ID 및 제목 키워드를 조건으로 하여 필터링된 게시글 목록을 조회합니다.
     *
     * @param request 검색 조건 파라미터가 담긴 Request DTO
     * @return 검색 조건에 부합하는 게시글 응답 DTO 리스트
     */
    List<PostResponse> searchPosts(PostSearchRequest request);
>>>>>>> 4fcc35de4764cbdb3747f46c6a2d5bb3bbf4a70b
}