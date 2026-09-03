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
import seungeasy.crewnavigator.domain.group.dto.response.GroupWarningResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupWarningSummaryResponse;
import seungeasy.crewnavigator.domain.group.service.GroupCommandService;
import seungeasy.crewnavigator.domain.group.service.GroupQueryService;

import java.util.List;

/**
 * <pre>
 *  Class Name: AdminGroupController
 *  Description: 관리자 전용 그룹 관리 API를 처리하는 컨트롤러.
 *
 *  [제공 API]
 *  - 그룹 경고 부여 (ADMIN)
 *  - 그룹 경고 누적 현황 조회 (ADMIN)
 *  - 그룹 경고 상세 조회 (ADMIN)
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 컨트롤러 생성
 * 2026.09.02: Seung-Geon: 그룹 경고 누적 현황/상세 조회 API 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
@Slf4j
@RestController
@RequestMapping("/admin/group")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "관리자 그룹 API", description = "관리자 전용 그룹 관리 API (그룹 경고 부여)")
public class AdminGroupController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;

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

    /**
     * 경고가 있는 그룹들의 누적 경고 현황을 조회합니다. (어드민 전용)
     *
     * @param adminDetails 관리자 인증 정보
     * @return 그룹별 경고 누적 목록
     */
    @Operation(summary = "그룹 경고 누적 현황 조회", description = "경고가 있는 그룹들의 누적 경고 현황을 조회합니다. (어드민 전용)")
    @GetMapping("/warnings")
    public ResponseEntity<CustomResponse<List<GroupWarningSummaryResponse>>> getWarningSummaries(
            @AuthenticationPrincipal CustomUserDetails adminDetails
    ) {
        if (adminDetails == null) {
            log.error("Get group warning summaries failed: Unauthorized admin context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        List<GroupWarningSummaryResponse> summaries = groupQueryService.getWarningSummaries();
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, summaries));
    }

    /**
     * 특정 그룹의 전체 경고 내역을 조회합니다. (어드민 전용, 멤버 검증 없이 조회)
     *
     * @param adminDetails 관리자 인증 정보
     * @param groupId      대상 그룹 번호
     * @return 그룹 경고 목록
     */
    @Operation(summary = "그룹 경고 상세 조회", description = "특정 그룹의 전체 경고 내역을 조회합니다. (어드민 전용)")
    @GetMapping("/warnings/{groupId}")
    public ResponseEntity<CustomResponse<List<GroupWarningResponse>>> getGroupWarnings(
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (adminDetails == null) {
            log.error("Get group warnings failed: Unauthorized admin context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        List<GroupWarningResponse> warnings = groupQueryService.getAdminGroupWarnings(groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, warnings));
    }
}
