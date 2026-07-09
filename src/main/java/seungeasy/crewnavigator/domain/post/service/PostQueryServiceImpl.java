package seungeasy.crewnavigator.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * 2026.07.09: Chi-Yoon: 공통 규격 벤치마킹을 위한 페이징 처리(Pageable, Page.map) 로직 구현 (💡 추가)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;

    /**
     * 카테고리 ID 및 제목 키워드를 기반으로 레포지토리에서 데이터를 페이징 조회하고,
     * 응답용 DTO 객체(PostResponse)로 변환하여 반환합니다.
     */
    @Override
    public Page<PostResponse> searchPosts(PostSearchRequest request, int page, int size) {
        log.info("Searching posts with paging - CategoryID: {}, Title Keyword: {}, Page: {}, Size: {}",
                request.categoryId(), request.title(), page, size);

        // 1. 최신 작성일자 순(DESC) 정렬 조건을 포함한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 2. 레포지토리를 호출하여 페이징 쿼리 수행 후, Page.map()을 통해 엔티티를 DTO로 변환하여 반환
        return postRepository.searchPosts(request.categoryId(), request.title(), pageable)
                .map(PostResponse::from);
    }
}