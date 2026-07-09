package seungeasy.crewnavigator.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.post.dto.request.PostSearchRequest;
import seungeasy.crewnavigator.domain.post.dto.response.PostResponse;
import seungeasy.crewnavigator.domain.post.repository.PostRepository;

/**
 * <pre>
 * Class Name: PostQueryServiceImpl
 * Description: 게시글(Post) 관련 읽기/조회(Query) 작업을 처리하는 서비스 구현체.
 * 성능 최적화를 위해 클래스 레벨에 @Transactional(readOnly = true)을 적용합니다.
 *
 * History
 * 2026.07.07: Chi-Yoon: searchPosts 동적 조회 비즈니스 로직 및 DTO 변환 스트림 파이프라인 구현
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;

    /**
     * 카테고리 ID 및 제목 키워드를 기반으로 레포지토리에서 데이터를 조회하고,
     * 응답용 DTO 객체(PostResponse)로 변환하여 반환합니다.
     */
    @Override
    public List<PostResponse> searchPosts(PostSearchRequest request) {
        log.info("Searching posts with conditions - CategoryID: {}, Title Keyword: {}",
                request.categoryId(), request.title());

        // 1. 레포지토리를 호출하여 다중 조건 검색 수행 (List<Post> 획득)
        return postRepository.searchPosts(request.categoryId(), request.title())
                .stream()
                // 2. 정적 팩토리 메서드(from)를 활용해 엔티티를 DTO로 변환
                .map(PostResponse::from)
                // 3. 변환된 DTO들을 최종 List 형태로 수집하여 반환
                .collect(Collectors.toList());
    }
}