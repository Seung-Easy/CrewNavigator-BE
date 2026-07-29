package seungeasy.crewnavigator.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.user.dto.request.UserInfoChangeRequest;
import seungeasy.crewnavigator.domain.user.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;
import seungeasy.crewnavigator.domain.user.service.UserCommandService;
import seungeasy.crewnavigator.domain.user.service.UserQueryService;

/**
 * <pre>
 *  Class Name: UserController
 *  Description: 사용자 프로필 관련 REST API 요청을 처리하는 컨트롤러.
 *
 *  [제공 API]
 *  - 내 정보 조회, 변경
 *  - 프로필 이미지 등록, 변경, 삭제
 *
 * History
 * 2026.07.15: Seung-Geon: 클래스 생성, 내 정보 조회 도메인 이동(auth->user), 내 정보 변경
 * 2026.07.16: Seung-Geon: 프로필 이미지 등록, 삭제 추가, @Tag 추가, Javadoc 수정
 </pre>
 *
 *  @author Seung-Geon
 * @version 1.0
 */

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 프로필 조회, 변경, 이미지 관리 API")
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
    @GetMapping("/profile")
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
    @PutMapping("/profile")
    public ResponseEntity<CustomResponse<Void>> changeMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserInfoChangeRequest request
    ){

        userCommandService.changeMyInfo(userDetails.getUsername(), request);
        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
    
    /**
     * 프로필 사진을 등록(변경)합니다.
     *
     * @param userDetails 인증된 사용자 정보
     * @param file        업로드할 이미지 파일 (jpg, jpeg, png, gif, webp)
     */
    @Operation(summary = "프로필 사진 등록(변경)", description = "현재 로그인된 사용자의 프로필 사진을 등록(변경)합니다. 허용 파일: jpg, jpeg, png, gif, webp / 최대 5MB")
    @PutMapping("/profile/image")
    public ResponseEntity<CustomResponse<Void>> uploadImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        userCommandService.uploadImage(userDetails.getUsername(), file);

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }

    /**
     * 프로필 이미지를 삭제하고 기본 이미지로 대체합니다.
     *
     * @param userDetails 인증된 사용자 정보
     */
    @Operation(summary = "프로필 이미지 삭제", description = "현재 로그인된 사용자의 프로필 사진을 삭제하고, 기본이미지로 대체")
    @DeleteMapping("/profile/image")
    public ResponseEntity<CustomResponse<Void>> deleteImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userCommandService.deleteImage(userDetails.getUsername());

        return ResponseEntity.ok(CustomResponse.success(ResponseCode.OK));
    }
}
