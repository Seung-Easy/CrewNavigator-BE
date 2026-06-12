package seungeasy.crewnavigator.domain.auth.service;

import seungeasy.crewnavigator.domain.auth.dto.request.*;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;

/**
 * <pre>
 *  Interface Name: AuthCommandService
 *  Description: 인증/계정 관련 쓰기(Command) 작업을 정의한 서비스 인터페이스.
 *
 *  [제공 기능]
 *  - 회원가입, 토큰 갱신, 로그아웃
 *  - 비밀번호 변경/재설정, 회원 탈퇴, 강제 로그아웃
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 인터페이스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface AuthCommandService {

    /**
     * 신규 사용자를 등록합니다.
     *
     * @param request 회원가입에 필요한 사용자 정보 (userId, password, name, email, phone 등)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 아이디 또는 이메일 중복 시
     */
    void signup(SignupRequest request);

    /**
     * Refresh Token을 검증하고 새로운 Access Token을 발급합니다.
     *
     * @param refreshToken HttpOnly Cookie로 전달된 Refresh Token
     * @return 새로운 Access Token 정보 (TokenResponse)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 토큰이 유효하지 않거나 Redis와 불일치 시
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * 현재 사용자를 로그아웃 처리합니다. Access Token을 블랙리스트에 등록하고 Redis에서 Refresh Token을 삭제합니다.
     *
     * @param accessToken 블랙리스트에 등록할 Access Token
     * @param userId      로그아웃할 사용자 ID
     */
    void logout(String accessToken, String userId);

    /**
     * 현재 비밀번호를 확인하고 새 비밀번호로 변경합니다.
     *
     * @param userId  비밀번호를 변경할 사용자 ID
     * @param request 현재 비밀번호와 새 비밀번호 정보
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없거나 현재 비밀번호가 일치하지 않을 시
     */
    void changePassword(String userId, PasswordChangeRequest request);

    /**
     * 아이디와 이메일 인증을 통해 비밀번호를 재설정합니다.
     *
     * @param request 사용자 ID와 이메일, 새 비밀번호 정보
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없을 시
     */
    void resetPassword(PasswordResetRequest request);

    /**
     * 계정을 탈퇴 처리합니다. (soft delete — status를 LEAVE로 변경)
     *
     * @param userId 탈퇴할 사용자 ID
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없을 시
     */
    void deleteAccount(String userId);

    /**
     * 관리자가 특정 사용자를 강제 로그아웃 처리합니다. Redis에서 Refresh Token을 삭제합니다.
     *
     * @param userId  강제 로그아웃 대상 사용자 ID
     * @param adminId 요청을 수행한 관리자 ID
     */
    void forceLogout(String userId, String adminId);
}
