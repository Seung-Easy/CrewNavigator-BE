package seungeasy.crewnavigator.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.auth.service.AuthCommandService;

/**
 * <pre>
 *  Class Name: AuthAdminController
 *  Description: 관리자 전용 인증/계정 관리 API를 처리하는 컨트롤러.
 *
 *  [제공 API]
 *  - 강제 로그아웃
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성 (forceLogout AuthController에서 분리)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Tag(name = "관리자 인증 API", description = "관리자 전용 계정 관리 API (강제 로그아웃 등)")
public class AuthAdminController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "강제 로그아웃", description = "관리자가 특정 사용자를 강제 로그아웃 처리합니다.")
    @PostMapping("/force-logout/{userId}")
    public ResponseEntity<CustomResponse<Void>> forceLogout(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable String userId) {
        authCommandService.forceLogout(userId, adminDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}
