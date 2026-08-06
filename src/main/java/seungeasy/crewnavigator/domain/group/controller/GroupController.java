package seungeasy.crewnavigator.domain.group.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.group.dto.request.GroupCreateRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupInviteRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupMemberRemoveRequestDto;
import seungeasy.crewnavigator.domain.group.dto.request.GroupUpdateRequestDto;
import seungeasy.crewnavigator.domain.group.dto.response.ApplicantResponse;
import seungeasy.crewnavigator.domain.group.dto.response.GroupResponse;
import seungeasy.crewnavigator.domain.group.dto.response.MemberResponse;
import seungeasy.crewnavigator.domain.group.service.GroupCommandService;
import seungeasy.crewnavigator.domain.group.service.GroupQueryService;
import seungeasy.crewnavigator.domain.group.type.GroupMemberRole;

import java.util.List;

/**
 * <pre>
 *  Class Name: GroupController
 *  Description:
 *
 *  [제공 API]
 *  - 그룹장 기능
 *      - 그룹 생성
 *      - 그룹원 초대
 *      - 그룹원 추방
 *      - 입장 신청 인원 정보 보기
 *      - 입장 신청 승인/거절
 *      - 그룹 정보 수정
 *      - 그룹 삭제
 *      - 멤버 권한 변경 (그룹장 위임 포함)
 *  - 그룹원 기능
 *      - 그룹 입장 신청
 *      - 그룹 입장 신청 취소
 *      - 그룹 정보 보기
 *      - 그룹원 정보 보기
 *      - 그룹용 게시판
 *      - 그룹 나가기
 *      - 그룹원 초대 수락/거절
 *
 *      - 그룹 검색
 *      - 입장 신청한 그룹들 보기
 *      - 내가 들어있는 그룹보기
 *
 * History
 * 2024.07.23: Seung-Geon: 클래스 생성
 * 2026.07.31: Seung-Geon: 누락 API(입장 승인/거절, 그룹 정보 수정/보기, 멤버 권한 변경) 추가 및 @AuthenticationPrincipal null 방어 적용
 * 2026.08.02: Seung-Geon: GroupCommandService/GroupQueryService 구현체 연동 및 요청 DTO 바인딩 적용
 * </pre>
 *
 *  @author Seung-Geon
 * @version 1.5
 */


@Slf4j
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Tag(name = "그룹을 관리하기 위한 API", description = "그룹을 만들고 관리하기 위한 API")
public class GroupController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;

    // =================================================================
    // 그룹장 기능
    // =================================================================

    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    @PostMapping("/regist")
    public ResponseEntity<CustomResponse<Void>> registGroup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody GroupCreateRequestDto request
    ) {
        if (userDetails == null) {
            log.error("Group creation failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.createGroup(userDetails.getUsername(), request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.CREATED));
    }

    @Operation(summary = "그룹원 초대", description = "그룹장이 원하는 인원을 그룹원으로 초대합니다.")
    @PostMapping("/{groupId}/member/invite")
    public ResponseEntity<CustomResponse<Void>> inviteGroupMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestBody GroupInviteRequestDto request
    ) {
        if (userDetails == null) {
            log.error("Invite member failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.inviteMember(userDetails.getUsername(), groupId, request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.CREATED));
    }

    @Operation(summary = "그룹원 추방", description = "그룹장이 원하는 그룹원을 추방합니다.")
    @DeleteMapping("/{groupId}/member/remove")
    public ResponseEntity<CustomResponse<Void>> removeGroupMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestBody GroupMemberRemoveRequestDto request
    ) {
        if (userDetails == null) {
            log.error("Remove member failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.removeMember(userDetails.getUsername(), groupId, request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.NO_CONTENT));
    }

    @Operation(summary = "입장 신청 인원 정보 보기", description = "그룹장이 그룹 신청 인원의 정보를 봅니다.")
    @GetMapping("/{groupId}/applicants")
    public ResponseEntity<CustomResponse<List<ApplicantResponse>>> getGroupApplicants(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (userDetails == null) {
            log.error("Get applicants failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        List<ApplicantResponse> applicants = groupQueryService.getApplicants(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, applicants));
    }

    @Operation(summary = "입장 신청 승인", description = "그룹장이 입장 신청한 인원의 가입을 승인합니다.")
    @PostMapping("/{groupId}/applicants/{groupMemberId}/approve")
    public ResponseEntity<CustomResponse<Void>> approveApplicant(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("groupMemberId") Long groupMemberId
    ) {
        if (userDetails == null) {
            log.error("Approve applicant failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.approveApplicant(userDetails.getUsername(), groupId, groupMemberId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "입장 신청 거절", description = "그룹장이 입장 신청한 인원의 가입을 거절합니다.")
    @PostMapping("/{groupId}/applicants/{groupMemberId}/reject")
    public ResponseEntity<CustomResponse<Void>> rejectApplicant(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("groupMemberId") Long groupMemberId
    ) {
        if (userDetails == null) {
            log.error("Reject applicant failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.rejectApplicant(userDetails.getUsername(), groupId, groupMemberId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "그룹 정보 수정", description = "그룹장이 그룹 정보(이름, 소개, 정원, 공개 여부 등)를 수정합니다.")
    @PutMapping("/{groupId}")
    public ResponseEntity<CustomResponse<Void>> updateGroup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestBody GroupUpdateRequestDto request
    ) {
        if (userDetails == null) {
            log.error("Update group failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.updateGroup(userDetails.getUsername(), groupId, request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "그룹 삭제", description = "그룹장이 그룹을 해산시킵니다.")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<CustomResponse<Void>> deleteGroup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (userDetails == null) {
            log.error("Delete group failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.deleteGroup(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.NO_CONTENT));
    }

    @Operation(summary = "멤버 권한 변경", description = "그룹장이 그룹원의 권한을 변경합니다. (LEADER, MEMBER)")
    @PatchMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<CustomResponse<Void>> changeMemberRole(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") String userId,
            @RequestParam("role") GroupMemberRole role
    ) {
        if (userDetails == null) {
            log.error("Change member role failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.changeMemberRole(userDetails.getUsername(), groupId, userId, role);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    // =================================================================
    // 그룹원 기능
    // =================================================================

    @Operation(summary = "그룹 검색", description = "키워드로 그룹을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<CustomResponse<List<GroupResponse>>> searchGroups(
            @RequestParam("keyword") String keyword
    ) {
        List<GroupResponse> groups = groupQueryService.searchGroups(keyword);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, groups));
    }

    @Operation(summary = "내가 가입한 그룹 보기", description = "현재 로그인한 사용자가 속한 그룹 목록을 봅니다.")
    @GetMapping("/my-groups")
    public ResponseEntity<CustomResponse<List<GroupResponse>>> getMyGroups(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            log.error("Get my groups failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        List<GroupResponse> myGroups = groupQueryService.getMyGroups(userDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, myGroups));
    }

    @Operation(summary = "입장 신청한 그룹 보기", description = "현재 로그인한 사용자가 입장 신청한 그룹 목록을 봅니다.")
    @GetMapping("/my-applications")
    public ResponseEntity<CustomResponse<List<GroupResponse>>> getMyAppliedGroups(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            log.error("Get my applied groups failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        List<GroupResponse> appliedGroups = groupQueryService.getMyAppliedGroups(userDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, appliedGroups));
    }

    @Operation(summary = "그룹 입장 신청", description = "그룹에 입장을 신청합니다.")
    @PostMapping("/{groupId}/applicants")
    public ResponseEntity<CustomResponse<Void>> applyToGroup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (userDetails == null) {
            log.error("Apply to group failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.applyToGroup(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.CREATED));
    }

    @Operation(summary = "그룹 입장 신청 취소", description = "그룹 입장 신청을 취소합니다.")
    @DeleteMapping("/{groupId}/applicants")
    public ResponseEntity<CustomResponse<Void>> cancelGroupApplication(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (userDetails == null) {
            log.error("Cancel group application failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.cancelGroupApplication(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.NO_CONTENT));
    }

    @Operation(summary = "그룹 정보 보기", description = "그룹의 상세 정보를 조회합니다.")
    @GetMapping("/{groupId}")
    public ResponseEntity<CustomResponse<GroupResponse>> getGroup(
            @PathVariable("groupId") Long groupId
    ) {
        GroupResponse group = groupQueryService.getGroup(groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, group));
    }

    @Operation(summary = "그룹원 정보 보기", description = "그룹에 속한 멤버들의 정보를 봅니다.")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<CustomResponse<List<MemberResponse>>> getGroupMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (userDetails == null) {
            log.error("Get group members failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        List<MemberResponse> members = groupQueryService.getGroupMembers(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, members));
    }

    @Operation(summary = "그룹 나가기", description = "멤버가 스스로 그룹을 나갑니다.")
    @DeleteMapping("/{groupId}/members/leave")
    public ResponseEntity<CustomResponse<Void>> leaveGroup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        if (userDetails == null) {
            log.error("Leave group failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.leaveGroup(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.NO_CONTENT));
    }

    @Operation(summary = "그룹 초대 응답", description = "그룹 초대를 수락하거나 거절합니다.")
    @PostMapping("/invitations/{invitationId}")
    public ResponseEntity<CustomResponse<Void>> respondToInvitation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("invitationId") Long invitationId,
            @RequestParam("accept") boolean accept
    ) {
        if (userDetails == null) {
            log.error("Respond to invitation failed: Unauthorized user context.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS));
        }

        groupCommandService.respondToInvitation(userDetails.getUsername(), invitationId, accept);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}
