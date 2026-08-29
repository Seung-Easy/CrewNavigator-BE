package seungeasy.crewnavigator.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.group.dto.request.GroupWarningRequestDto;
import seungeasy.crewnavigator.domain.group.service.GroupCommandService;

/**
 * <pre>
 *  Class Name: AdminGroupController
 *  Description: 관리자 전용 그룹 관리 API를 처리하는 컨트롤러.
 *
 *  [제공 API]
 *  - 그룹 경고 부여 (ADMIN)
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 컨트롤러 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/admin/group")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "관리자 그룹 API", description = "관리자 전용 그룹 관리 API (그룹 경고 부여)")
public class AdminGroupController {

    private final GroupCommandService groupCommandService;

    /**
     * 관리자가 특정 그룹에 경고를 부여합니다.
     *
     * @param adminDetails 관리자 인증 정보
     * @param groupId      대상 그룹 번호
     * @param request      경고 등록 요청 정보
     * @return 성공 응답
     */
    @Operation(summary = "그룹 경고 부여", description = "관리자가 특정 그룹에 경고를 부여합니다.")
    @PostMapping("/{groupId}/warning")
    public ResponseEntity<CustomResponse<Void>> warnGroup(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable Long groupId,
            @Valid @RequestBody GroupWarningRequestDto request
    ) {
        if (adminDetails == null) {
            log.error("Group warning failed: Unauthorized admin context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.warnGroup(adminDetails.getUsername(), groupId, request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}
