package seungeasy.crewnavigator.domain.post.service;

import java.util.List;
import seungeasy.crewnavigator.domain.post.dto.request.PostSearchRequest;
import seungeasy.crewnavigator.domain.post.dto.response.PostResponse;

/**
 * <pre>
 * Interface Name: PostQueryService
 * Description: 게시글(Post) 관련 읽기/조회(Query) 작업을 처리하는 서비스 인터페이스.
 *
 * History
 * 2026.07.07: Chi-Yoon: 게시글 다중 조건 검색을 위한 조회 메서드(searchPosts) 정의
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
public interface PostQueryService {

    /**
     * 카테고리 ID 및 제목 키워드를 조건으로 하여 필터링된 게시글 목록을 조회합니다.
     *
     * @param request 검색 조건 파라미터가 담긴 Request DTO
     * @return 검색 조건에 부합하는 게시글 응답 DTO 리스트
     */
    List<PostResponse> searchPosts(PostSearchRequest request);
}