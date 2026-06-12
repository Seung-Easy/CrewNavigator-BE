package seungeasy.crewnavigator.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.*;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.auth.service.AuthCommandService;
import seungeasy.crewnavigator.domain.auth.service.AuthQueryService;

import java.time.Duration;
import java.util.List;

/**
 * <pre>
 *  Class Name: AuthController
 *  Description: 인증/인가 관련 REST API 요청을 처리하는 컨트롤러.
 *
 *  [제공 API]
 *  - 회원가입, 로그인, 토큰 갱신, 로그아웃
 *  - 비밀번호 변경/재설정, 회원 탈퇴
 *  - 강제 로그아웃, 아이디 찾기, 내 정보 조회
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입, 토큰 관리 등 인증/인가 관련 API")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @GetMapping("/health")
    public String health() {
        return "Auth Health OK";
    }

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 새로운 계정을 생성합니다.")
    @PostMapping("/signup")
    public ResponseEntity<CustomResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authCommandService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomResponse.success(ResponseCode.CREATED));
    }

    @Operation(summary = "토큰 갱신", description = "HttpOnly 쿠키의 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<CustomResponse<TokenResponse>> refreshToken(
            @CookieValue("refreshToken") String refreshToken) {
        TokenResponse tokenResponse = authCommandService.refreshToken(refreshToken);

        // 새 Refresh Token을 HttpOnly Cookie로 갱신
        ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                .body(CustomResponse.success(ResponseCode.OK,
                        TokenResponse.of(tokenResponse.accessToken(), null, tokenResponse.expiresIn())));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 계정을 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<CustomResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.substring(7);
        authCommandService.logout(accessToken, userDetails.getUsername());

        // Refresh Token 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<CustomResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {
        authCommandService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "비밀번호 재설정", description = "아이디와 이메일 인증을 통해 비밀번호를 재설정합니다.")
    @PostMapping("/password/reset")
    public ResponseEntity<CustomResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authCommandService.resetPassword(request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "회원 탈퇴", description = "계정을 탈퇴 처리합니다.")
    @DeleteMapping("/account")
    public ResponseEntity<CustomResponse<Void>> deleteAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authCommandService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "강제 로그아웃", description = "관리자가 특정 사용자를 강제 로그아웃 처리합니다.")
    @PostMapping("/force-logout/{userId}")
    public ResponseEntity<CustomResponse<Void>> forceLogout(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable String userId) {
        authCommandService.forceLogout(userId, adminDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "아이디 찾기", description = "이름과 이메일로 가입된 아이디를 조회합니다.")
    @GetMapping("/find-id")
    public ResponseEntity<CustomResponse<List<String>>> findId(@Valid @RequestBody FindIdRequest request) {
        List<String> userIds = authQueryService.findUserId(request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, userIds));
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<CustomResponse<UserInfoResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserInfoResponse response = authQueryService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, response));
    }
}
