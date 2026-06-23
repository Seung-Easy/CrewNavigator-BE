package seungeasy.crewnavigator.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.ChangeRoleRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.ActiveSessionResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.AdminUserResponse;
import seungeasy.crewnavigator.domain.auth.dto.response.UserStatisticsResponse;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.auth.service.AuthCommandService;
import seungeasy.crewnavigator.domain.auth.service.AuthQueryService;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.util.List;

/**
 * <pre>
 *  Class Name: AuthAdminController
 *  Description: 관리자 전용 인증/계정 관리 API를 처리하는 컨트롤러.
 *
 *  [제공 API]
 *  - 강제 로그아웃 (ADMIN)
 *  - 계정 복구 LEAVE → INACTIVE (ADMIN)
 *  - 회원 목록 조회 (MANAGER 이상)
 *  - 회원 상세 조회 (MANAGER 이상)
 *  - 회원 통계 조회 (OPERATOR 이상)
 *  - 회원 권한 변경 (ADMIN)
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성 (forceLogout AuthController에서 분리)
 * 2026.06.16: Seung-Geon: /admin/auth/restore/{userId} 계정 복구 엔드포인트 추가 (LEAVE → INACTIVE)
 * 2026.06.16: Seung-Geon: RoleHierarchy 도입, @PreAuthorize 기반 권한 제어로 변경
 * 2026.06.16: Seung-Geon: searchUsers, getAdminUserDetail, getUserStatistics, changeUserRole 엔드포인트 추가
 * 2026.06.22: Seung-Geon: /admin/auth/sessions/active 엔드포인트 추가 (활성 세션 조회)
 * </pre>
 *
 *  @author Seung-Geon
 * @version 1.3
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 인증 API", description = "관리자 전용 계정 관리 API (회원 조회/권한 변경)")
public class AuthAdminController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @Operation(summary = "강제 로그아웃", description = "관리자가 특정 사용자를 강제 로그아웃 처리(refresh token 삭제)합니다.")
    @PostMapping("/force-logout/{userId}")
    public ResponseEntity<CustomResponse<Void>> forceLogout(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable String userId) {
        authCommandService.forceLogout(userId, adminDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "활성 세션(로그인 중) 조회", description = "현재 로그인 중(Redis에 refresh token이 존재)인 회원 목록을 조회합니다. force-logout 전 확인 용도로 사용합니다. (ADMIN 전용)")
    @GetMapping("/sessions/active")
    public ResponseEntity<CustomResponse<List<ActiveSessionResponse>>> getActiveSessions() {
        List<ActiveSessionResponse> sessions = authQueryService.getActiveSessions();
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, sessions));
    }

    @Operation(summary = "Role 목록 조회", description = "전체 Role 권한명 목록을 조회합니다. (MANAGER 이상 접근 가능)")
    @GetMapping("/roles")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CustomResponse<List<String>>> getRoles() {
        List<String> roles = authQueryService.getAllRoleNames();
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, roles));
    }

    @Operation(summary = "계정 복구 (LEAVE → INACTIVE)", description = "관리자가 탈퇴(LEAVE)한 계정을 비활성(INACTIVE) 상태로 복구합니다. 복구 후 사용자는 이메일 인증을 통해 계정을 재활성화할 수 있습니다.")
    @PutMapping("/restore/{userId}")
    public ResponseEntity<CustomResponse<Void>> restoreAccount(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable String userId) {
        authCommandService.restoreAccount(userId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "회원 목록 조회", description = "관리자용 회원 목록을 페이징하여 조회합니다. 상태(status)와 검색어(keyword)로 필터링 가능합니다. (MANAGER 이상 접근 가능)")
    @GetMapping("/users")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CustomResponse<Page<AdminUserResponse>>> searchUsers(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AdminUserResponse> result = authQueryService.searchUsers(status, keyword, page, size);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, result));
    }

    @Operation(summary = "회원 상세 조회", description = "관리자가 특정 회원의 상세 정보(권한 포함)를 조회합니다. (MANAGER 이상 접근 가능)")
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CustomResponse<AdminUserResponse>> getUserDetail(
            @PathVariable String userId) {
        AdminUserResponse result = authQueryService.getAdminUserDetail(userId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, result));
    }

    @Operation(summary = "회원 통계 조회", description = "회원 통계 정보를 조회합니다. (전체 수, 상태별/권한별 분포, 오늘 가입 수) (OPERATOR 이상 접근 가능)")
    @GetMapping("/users/statistics")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<CustomResponse<UserStatisticsResponse>> getUserStatistics() {
        UserStatisticsResponse result = authQueryService.getUserStatistics();
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, result));
    }

    @Operation(summary = "회원 권한 변경", description = "특정 회원의 권한을 변경합니다. 기존 권한은 제거되고 새 권한이 부여됩니다. (ADMIN 전용)")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<CustomResponse<Void>> changeUserRole(
            @PathVariable String userId,
            @Valid @RequestBody ChangeRoleRequest request) {
        authCommandService.changeUserRole(userId, request.roleName());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}
