package seungeasy.crewnavigator.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.post.dto.request.NoticeCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.NoticeUpdateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostSearchRequest;
import seungeasy.crewnavigator.domain.post.dto.response.PostResponse;
import seungeasy.crewnavigator.domain.post.service.PostCommandService;
import seungeasy.crewnavigator.domain.post.service.PostQueryService;

/**
 * <pre>
 * Class Name: PostAdminController
 * Description: 관리자 전용 게시글(Post) 관리 API를 처리하는 컨트롤러 클래스.
 *
 * [제공 API]
 * - 삭제되거나 신고된 게시글을 포함한 전체 게시글 페이징 조회 (MANAGER 이상)
 * - 관리자 권한을 이용한 특정 게시글 강제 삭제/블라인드 (MANAGER 이상)
 * - 관리자 전용 공지사항 작성 및 수정 (MANAGER 이상)
 *
 * History
 * 2026.07.25: Chi-Yoon: 관리자 전용 게시글 관리 컨트롤러 최초 생성
 * 2026.07.28: Chi-Yoon: 공지사항 작성(createNotice) 및 수정(updateNotice) API 추가
 * 2026.08.01: Chi-Yoon: ROLE_MANAGER 권한까지 허용하도록 @PreAuthorize 세분화 적용
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.2
 */
@Slf4j
@RestController
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // 💡 기본 접근 제어를 MANAGER 이상으로 변경
@Tag(name = "관리자 게시글 API", description = "관리자 전용 게시글 관리 API (강제 삭제, 공지사항 관리 및 전체 조회)")
public class PostAdminController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Operation(summary = "관리자용 전체 게시글 목록 조회", description = "관리자가 시스템 전체 게시글을 조회합니다. (MANAGER 이상 접근 가능)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomResponse<Page<PostResponse>>> searchPostsForAdmin(
            @ModelAttribute PostSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Admin API Request - Search All Posts: Page={}, Size={}", page, size);

        // TODO: 관리자 전용 조회 메서드가 필요하다면 QueryService에 추가
        Page<PostResponse> posts = postQueryService.searchPosts(request, page, size);

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, posts));
    }

    @Operation(summary = "게시글 강제 삭제", description = "관리자 권한으로 특정 게시글을 강제 삭제(또는 숨김 처리)합니다. (MANAGER 이상 접근 가능)")
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomResponse<Void>> forceDeletePost(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable("postId") Long postId
    ) {
        log.info("Admin API Request - Force Delete Post ID: {} by Admin/Manager: {}", postId, adminDetails.getUsername());

        postCommandService.forceDeletePostByAdmin(postId, adminDetails.getUsername());

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "공지사항 작성", description = "관리자 권한으로 새로운 공지사항을 등록합니다. (MANAGER 이상 접근 가능)")
    @PostMapping("/notices")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomResponse<Long>> createNotice(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @Valid @RequestBody NoticeCreateRequest request
    ) {
        log.info("Admin API Request - Create Notice by Admin/Manager: {}", adminDetails.getUsername());

        Long noticeId = postCommandService.createNoticeByAdmin(request, adminDetails.getUsername());

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, noticeId));
    }

    @Operation(summary = "공지사항 수정", description = "관리자 권한으로 작성된 공지사항을 수정합니다. (MANAGER 이상 접근 가능)")
    @PutMapping("/notices/{noticeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomResponse<Void>> updateNotice(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable("noticeId") Long noticeId,
            @Valid @RequestBody NoticeUpdateRequest request
    ) {
        log.info("Admin API Request - Update Notice ID: {} by Admin/Manager: {}", noticeId, adminDetails.getUsername());

        postCommandService.updateNoticeByAdmin(noticeId, request, adminDetails.getUsername());

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}