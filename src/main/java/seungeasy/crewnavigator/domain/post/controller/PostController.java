package seungeasy.crewnavigator.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.post.dto.request.PostCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostUpdateRequest; // 추가된 DTO 임포트
import seungeasy.crewnavigator.domain.post.service.PostCommandService;

/**
 * <pre>
 * Class Name: PostController
 * Description: 게시글(Post) 관련 API 요청을 처리하는 컨트롤러 클래스.
 *
 * History
 * 2026.06.27: Chi-Yoon: NullPointerException 해결을 위한 @AuthenticationPrincipal 타입 수정
 * 2026.07.01: Chi-Yoon: 프로젝트 CustomResponse 및 UserDetails 규격에 맞춘 게시글 수정/삭제 API 추가 (수정)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.3
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostCommandService postCommandService;

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
     * 기존 게시글을 수정합니다. (💡 추가)
     * URL 구조: PUT /api/v1/posts/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<CustomResponse<Void>> updatePost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        // 인증 유저 방어 코드
        if (loginUser == null) {
            log.error("Post update failed: Unauthorized user context. Post ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Update Post ID: {} by User ID: {}", postId, loginUser.getUsername());

        // 서비스 레이어 호출 (내부에서 작성자 일치 여부 EP002 검증 진행)
        postCommandService.updatePost(postId, request, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }

    /**
     * 기존 게시글을 삭제(소프트 딜리트)합니다. (💡 추가)
     * URL 구조: DELETE /api/v1/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<CustomResponse<Void>> deletePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        // 인증 유저 방어 코드
        if (loginUser == null) {
            log.error("Post deletion failed: Unauthorized user context. Post ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Delete Post ID: {} by User ID: {}", postId, loginUser.getUsername());

        // 서비스 레이어 호출 (내부에서 작성자 일치 여부 EP002 검증 진행)
        postCommandService.deletePost(postId, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }
}