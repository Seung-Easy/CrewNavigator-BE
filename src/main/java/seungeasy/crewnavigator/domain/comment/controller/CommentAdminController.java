package seungeasy.crewnavigator.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.comment.service.CommentCommandService;

/**
 * <pre>
 * Class Name: CommentAdminController
 * Description: 관리자 전용 댓글(Comment) 관리 API를 처리하는 컨트롤러 클래스.
 *
 * [제공 API]
 * - 관리자 권한을 이용한 특정 댓글 강제 삭제 (MANAGER 이상)
 *
 * History
 * 2026.07.25: Chi-Yoon: 관리자 전용 댓글 관리 컨트롤러 최초 생성
 * 2026.08.01: Chi-Yoon: ROLE_MANAGER 권한까지 허용하도록 @PreAuthorize 세분화 적용
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
 */
@Slf4j
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // 💡 기본 접근 제어를 MANAGER 이상으로 변경
@Tag(name = "관리자 댓글 API", description = "관리자 전용 댓글 관리 API (강제 삭제 등)")
public class CommentAdminController {

    private final CommentCommandService commentCommandService;

    @Operation(summary = "댓글 강제 삭제", description = "관리자 권한으로 특정 댓글을 작성자 검증 없이 강제 삭제합니다. (MANAGER 이상 접근 가능)")
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomResponse<Void>> forceDeleteComment(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable("commentId") Long commentId
    ) {
        log.info("Admin API Request - Force Delete Comment ID: {} by Admin/Manager: {}", commentId, adminDetails.getUsername());

        commentCommandService.forceDeleteCommentByAdmin(commentId, adminDetails.getUsername());

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}