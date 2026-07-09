package seungeasy.crewnavigator.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.post.dto.request.PostCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostSearchRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostUpdateRequest;
import seungeasy.crewnavigator.domain.post.dto.response.PostResponse;
import seungeasy.crewnavigator.domain.post.service.PostCommandService;
import seungeasy.crewnavigator.domain.post.service.PostQueryService;

/**
 * <pre>
 * Class Name: PostController
 * Description: 게시글(Post) 관련 API 요청을 처리하는 컨트롤러 클래스.
 *
 * History
 * 2026.06.27: Chi-Yoon: NullPointerException 해결을 위한 @AuthenticationPrincipal 타입 수정
 * 2026.07.01: Chi-Yoon: 프로젝트 CustomResponse 및 UserDetails 규격에 맞춘 게시글 수정/삭제 API 추가
 * 2026.07.07: Chi-Yoon: PostQueryService 도입 및 카테고리/제목 다중 조건 검색 API(searchPosts) 추가
 * 2026.07.09: Chi-Yoon: 공통 관리자 API 규격을 벤치마킹하여 게시글 다중 조회에 페이징 처리(Page) 도입 (💡 추가)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.5
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService; // 읽기 전용 쿼리 서비스 주입

    /**
     * 카테고리 ID 및 제목 키워드를 기반으로 필터링된 게시글 목록을 페이징하여 다중 조건 검색합니다. (💡 수정)
     * URL 구조: GET /api/v1/posts?categoryId=1&title=키워드&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<CustomResponse<Page<PostResponse>>> searchPosts(
            @ModelAttribute PostSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("API Request - Search Posts Conditions: CategoryID={}, Title={}, Page={}, Size={}",
                request.categoryId(), request.title(), page, size);

        Page<PostResponse> posts = postQueryService.searchPosts(request, page, size);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK, posts));
    }

    /**
     * 새로운 게시글을 등록합니다.
     */
    @PostMapping
    public ResponseEntity<CustomResponse<Long>> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        if (loginUser == null) {
            log.error("Post creation failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Create Post by User ID: {}", loginUser.getUsername());

        Long postId = postCommandService.createPost(request, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.success(ResponseCode.CREATED, postId));
    }

    /**
     * 기존 게시글을 수정합니다.
     * URL 구조: PUT /api/v1/posts/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<CustomResponse<Void>> updatePost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        if (loginUser == null) {
            log.error("Post update failed: Unauthorized user context. Post ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Update Post ID: {} by User ID: {}", postId, loginUser.getUsername());

        postCommandService.updatePost(postId, request, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }

    /**
     * 기존 게시글을 삭제(소프트 딜리트)합니다.
     * URL 구조: DELETE /api/v1/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<CustomResponse<Void>> deletePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        if (loginUser == null) {
            log.error("Post deletion failed: Unauthorized user context. Post ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Delete Post ID: {} by User ID: {}", postId, loginUser.getUsername());

        postCommandService.deletePost(postId, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }
}