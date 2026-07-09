package seungeasy.crewnavigator.domain.comment.controller;

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
import seungeasy.crewnavigator.domain.comment.dto.request.CommentCreateRequest;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentSearchRequest;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentUpdateRequest;
import seungeasy.crewnavigator.domain.comment.dto.response.CommentResponse;
import seungeasy.crewnavigator.domain.comment.service.CommentCommandService;
import seungeasy.crewnavigator.domain.comment.service.CommentQueryService;

/**
 * <pre>
 * Class Name: CommentController
 * Description: 댓글(Comment) 관련 API 요청을 처리하는 컨트롤러 클래스.
 *
 * History
 * 2026.07.05: Chi-Yoon: 프로젝트 CustomResponse 및 UserDetails 규격에 맞춘 댓글 등록 API 최초 생성
 * 2026.07.05: Chi-Yoon: 댓글 수정(PUT) 및 삭제(DELETE) API 추가 확장 및 권한 방어 적용
 * 2026.07.07: Chi-Yoon: CommentQueryService 도입 및 게시글 번호/유저 ID 다중 조건 검색 API(searchComments) 추가
 * 2026.07.09: Chi-Yoon: 공통 관리자 API 규격을 벤치마킹하여 댓글 다중 조회에 페이징 처리(Page) 도입 (💡 추가)
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.3
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService; // 읽기 전용 쿼리 서비스 주입

    /**
     * 게시글 식별 번호 및 사용자 아이디를 기반으로 필터링된 댓글 목록을 페이징하여 다중 조건 검색합니다. (💡 수정)
     * URL 구조: GET /api/v1/comments?postId=1&userId=easy123&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<CustomResponse<Page<CommentResponse>>> searchComments(
            @ModelAttribute CommentSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("API Request - Search Comments Conditions: PostID={}, UserID={}, Page={}, Size={}",
                request.postId(), request.userId(), page, size);

        Page<CommentResponse> comments = commentQueryService.searchComments(request, page, size);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK, comments));
    }

    /**
     * 특정 게시글에 새로운 댓글을 등록합니다.
     * URL 구조: POST /api/v1/comments
     */
    @PostMapping
    public ResponseEntity<CustomResponse<Long>> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        if (loginUser == null) {
            log.error("Comment creation failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Create Comment on Post ID: {} by User ID: {}",
                request.postId(), loginUser.getUsername());

        Long commentId = commentCommandService.createComment(request, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.success(ResponseCode.CREATED, commentId));
    }

    /**
     * 기존 댓글을 수정합니다.
     * URL 구조: PUT /api/v1/comments/{commentId}
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CustomResponse<Void>> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        if (loginUser == null) {
            log.error("Comment update failed: Unauthorized user context. Comment ID: {}", commentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Update Comment ID: {} by User ID: {}", commentId, loginUser.getUsername());

        commentCommandService.updateComment(commentId, request, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }

    /**
     * 기존 댓글을 삭제(소프트 딜리트)합니다.
     * URL 구조: DELETE /api/v1/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CustomResponse<Void>> deleteComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        if (loginUser == null) {
            log.error("Comment deletion failed: Unauthorized user context. Comment ID: {}", commentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Delete Comment ID: {} by User ID: {}", commentId, loginUser.getUsername());

        commentCommandService.deleteComment(commentId, loginUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }
}