package seungeasy.crewnavigator.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.user.dto.request.UserInfoChangeRequest;
import seungeasy.crewnavigator.domain.user.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.user.service.UserCommandService;
import seungeasy.crewnavigator.domain.user.service.UserQueryService;

/**
 * <pre>
 *  Class Name: userController
 *  Description:
 *
 *  [제공 API]
 *  - 내 정보 조회, 변경
 *  - 프로필 이미지 등록, 변경, 삭제
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성, 내 정보 조회 도메인 이동(auth->user), 내 정보 변경
 </pre>
 *
 *  @author Seung-Geon
 * @version 1.0
 */

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    /**
     * 내 정보를 조회합니다.
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 프로필 정보
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<CustomResponse<UserInfoResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserInfoResponse response = userQueryService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK, response));
    }

    /**
     * 내 정보를 변경합니다.
     *
     * @param userDetails 인증된 사용자 정보
     * @param request     변경할 정보 (이름, 생일, 주소, 전화번호, 프로필 이미지)
     */
    @Operation(summary = "내 정보 변경", description = "현재 로그인된 사용자의 정보를 변경합니다.")
    @PutMapping("/me")
    public ResponseEntity<CustomResponse<Void>> changeMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserInfoChangeRequest request
    ){

        userCommandService.changeMyInfo(userDetails.getUsername(), request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    // TODO: 프로필 사진 등록(변경) 구현 필요
    // TODO: 프로필 사진 삭제 구현 필요
}
