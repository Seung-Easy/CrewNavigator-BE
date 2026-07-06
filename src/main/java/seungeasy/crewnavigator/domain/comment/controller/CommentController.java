package seungeasy.crewnavigator.domain.comment.controller;

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
import seungeasy.crewnavigator.domain.comment.dto.request.CommentCreateRequest;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentUpdateRequest; // 💡 임포트 추가
import seungeasy.crewnavigator.domain.comment.service.CommentCommandService;

/**
 * <pre>
 * Class Name: CommentController
 * Description: 댓글(Comment) 관련 API 요청을 처리하는 컨트롤러 클래스.
 *
 * History
 * 2026.07.05: Chi-Yoon: 프로젝트 CustomResponse 및 UserDetails 규격에 맞춘 댓글 등록 API 최초 생성
 * 2026.07.05: Chi-Yoon: 댓글 수정(PUT) 및 삭제(DELETE) API 추가 확장 및 권한 방어 적용
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentCommandService commentCommandService;

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
        // 1. 시큐리티 인증 유저 컨텍스트 누락 방어
        if (loginUser == null) {
            log.error("Comment update failed: Unauthorized user context. Comment ID: {}", commentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Update Comment ID: {} by User ID: {}", commentId, loginUser.getUsername());

        // 2. 서비스 레이어 호출 및 비즈니스 로직(원작자 검증 포함) 수행
        commentCommandService.updateComment(commentId, request, loginUser.getUsername());

        // 3. 성공 응답 반환 (S001 OK 규격 적용)
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
        // 1. 시큐리티 인증 유저 컨텍스트 누락 방어
        if (loginUser == null) {
            log.error("Comment deletion failed: Unauthorized user context. Comment ID: {}", commentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        log.info("API Request - Delete Comment ID: {} by User ID: {}", commentId, loginUser.getUsername());

        // 2. 서비스 레이어 호출 및 비즈니스 로직(원작자 검증 포함) 수행
        commentCommandService.deleteComment(commentId, loginUser.getUsername());

        // 3. 성공 응답 반환 (S001 OK 규격 적용)
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.success(ResponseCode.OK));
    }
}