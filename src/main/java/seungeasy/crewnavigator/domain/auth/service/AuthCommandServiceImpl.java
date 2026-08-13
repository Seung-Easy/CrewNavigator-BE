package seungeasy.crewnavigator.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.infra.redis.RedisService;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.*;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;
import seungeasy.crewnavigator.domain.auth.entity.*;
import seungeasy.crewnavigator.domain.auth.infra.EmailService;
import seungeasy.crewnavigator.domain.auth.repository.*;
import seungeasy.crewnavigator.domain.auth.security.JwtProvider;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <pre>
 *  Class Name: AuthCommandServiceImpl
 *  Description: 인증/계정 관련 쓰기(Command) 작업을 처리하는 서비스 구현체.
 *
 *  [주요 기능]
 *  - 회원가입, 토큰 갱신, 로그아웃
 *  - 비밀번호 변경/재설정, 회원 탈퇴, 강제 로그아웃
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.15: Seung-Geon: sendVerificationCode, verifyEmailCode 구현, signup 이메일 인증 확인 로직 추가, resetPassword 코드 검증 + 계정 잠금 해제 로직 추가
 * 2026.06.15: Seung-Geon: resetPassword email:verified 키 검증 방식으로 변경 (code 직접 입력 → verify-code 선행)
 * 2026.06.16: Seung-Geon: sendResetCode 메서드 구현 (userId+email 검증 후 reset type으로 코드 발송)
 * 2026.06.16: Seung-Geon: verifyEmailCode, signup, resetPassword Redis 키에 type 포함하도록 변경 (용도 분리)
 * 2026.06.16: Seung-Geon: restoreAccount, reactivateAccount 메서드 구현
 * 2026.06.16: Seung-Geon: sendVerificationCode reactivate type 처리 추가 (INACTIVE 사용자 확인)
 * 2026.06.16: Seung-Geon: changeUserRole 구현 (기존 권한 제거 + 새 권한 부여)
 * 2026.06.22: Seung-Geon: forceLogout is_locked + blacklist:user:{userId} 저장, resetPassword unlock 시 blacklist 정리
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final EmailService emailService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendVerificationCode(SendVerificationCodeRequest request) {
        // reactivate 요청 시 해당 이메일의 INACTIVE 사용자 존재 여부 확인
        if ("reactivate".equals(request.type())) {
            userRepository.findByEmail(request.email())
                    .filter(u -> u.getStatus() == UserStatus.INACTIVE)
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        }

        emailService.sendVerificationCode(request.email(), request.type());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendResetCode(SendResetCodeRequest request) {
        // userId + email 조합이 DB에 존재하는지 검증
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getUserId().equals(request.userId()))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 존재하면 인증코드 발송 (type=reset)
        emailService.sendVerificationCode(request.email(), "reset");
        log.info("Password reset code sent to {} for user: {}", request.email(), request.userId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyEmailCode(VerifyCodeRequest request) {
        String codeKey = "email:code:" + request.type() + ":" + request.email();
        Object storedCode = redisService.get(codeKey);
        
        if (storedCode == null) {
            throw new BusinessException(ResponseCode.EXPIRED_VERIFICATION_CODE);
        }
        if (!storedCode.toString().equals(request.code())) {
            throw new BusinessException(ResponseCode.INVALID_VERIFICATION_CODE);
        }
        
        redisService.delete(codeKey);
        // Mark email as verified for 30 minutes with type-specific key
        String verifiedKey = "email:verified:" + request.type() + ":" + request.email();
        redisService.save(verifiedKey, true, 30, TimeUnit.MINUTES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void signup(SignupRequest request) {
        // 이메일 인증 확인 (signup 용도)
        String verifiedKey = "email:verified:signup:" + request.email();
        if (!redisService.hasKey(verifiedKey)) {
            throw new BusinessException(ResponseCode.EMAIL_VERIFICATION_REQUIRED);
        }
        redisService.delete(verifiedKey);

        // 중복 확인
        if (userRepository.existsByUserId(request.userId())) {
            throw new BusinessException(ResponseCode.DUPLICATE_USER_ID);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ResponseCode.DUPLICATE_EMAIL);
        }

        User user = new User();
        user.setUserId(request.userId());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setGender(request.gender());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setBirthday(request.birthday() != null ? java.time.LocalDate.parse(request.birthday()) : null);
        user.setAddress(request.address());

        userRepository.save(user);

        // ROLE_USER 자동 부여
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new BusinessException(ResponseCode.INVALID_INPUT_VALUE));
        UserRole userRoleEntity = new UserRole(userRole.getRoleId(), request.userId());
        userRoleRepository.save(userRoleEntity);

        log.info("User signed up: {} (ROLE_USER assigned)", request.userId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ResponseCode.INVALID_TOKEN);
        }

        String userId = jwtProvider.getUserIdFromToken(refreshToken);

        // Redis에 저장된 Refresh Token과 비교
        String redisKey = "refresh:" + userId;
        String storedToken = (String) redisService.get(redisKey);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(ResponseCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 새 토큰 발급
        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        // Redis에 새 Refresh Token 저장 (기존 것은 덮어쓰기)
        redisService.save(redisKey, newRefreshToken, jwtProvider.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        log.info("Token refreshed for user: {}", userId);

        return TokenResponse.of(newAccessToken, newRefreshToken, jwtProvider.getAccessTokenExpiration() / 1000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void logout(String accessToken, String userId) {
        // Access Token을 블랙리스트에 등록 (만료될 때까지 유효하지 않도록)
        String blacklistKey = "blacklist:" + accessToken;
        redisService.save(blacklistKey, "LOGOUT", jwtProvider.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);

        // Redis에서 Refresh Token 삭제
        String redisKey = "refresh:" + userId;
        redisService.delete(redisKey);

        log.info("User logged out: {}", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void changePassword(String userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.INVALID_PASSWORD);
        }

        // 현재 비밀번호와 동일한지 검증
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.SAME_AS_CURRENT_PASSWORD);
        }

        // 최근 비밀번호 재사용 검증
        checkPasswordReuse(userId, request.newPassword());

        // 비밀번호 이력 저장
        PasswordHistory history = new PasswordHistory();
        history.setUserId(userId);
        history.setBeforeChangedPwd(user.getPassword());
        history.setChangedAt(LocalDateTime.now());
        passwordHistoryRepository.save(history);

        // 새 비밀번호로 변경
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        // 이메일 인증 확인 (reset 용도, verify-code 선행 필수)
        String verifiedKey = "email:verified:reset:" + request.email();
        if (!redisService.hasKey(verifiedKey)) {
            throw new BusinessException(ResponseCode.EMAIL_VERIFICATION_REQUIRED);
        }
        redisService.delete(verifiedKey);
        
        // 사용자 찾기
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getUserId().equals(request.userId()))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 현재 비밀번호와 동일한지 검증
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.SAME_AS_CURRENT_PASSWORD);
        }

        // 최근 비밀번호 재사용 검증
        checkPasswordReuse(user.getUserId(), request.newPassword());

        // 변경 전 비밀번호 백업 (history 저장용)
        String oldPassword = user.getPassword();
        
        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        
        // 계정이 잠겨있으면 해제
        if ("Y".equals(user.getIsLocked())) {
            user.setIsLocked("N");
            user.setLoginFailCount(0);

            // 강제 로그아웃 블랙리스트 해제 (forceLogout 시에만 존재, 없으면 skip)
            String userBlacklistKey = "blacklist:user:" + user.getUserId();
            if (redisService.hasKey(userBlacklistKey)) {
                redisService.delete(userBlacklistKey);
            }
        }
        
        userRepository.save(user);

        // 비밀번호 이력 저장
        PasswordHistory history = new PasswordHistory();
        history.setUserId(user.getUserId());
        history.setBeforeChangedPwd(oldPassword);
        history.setChangedAt(LocalDateTime.now());
        passwordHistoryRepository.save(history);

        log.info("Password reset for user: {}. Account locked: {}", request.userId(), "Y".equals(user.getIsLocked()) ? "해제됨" : "해당없음");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAccount(String userId, String accessToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // Refresh Token 삭제
        String redisKey = "refresh:" + userId;
        redisService.delete(redisKey);

        // Access Token 블랙리스트 등록
        String blacklistKey = "blacklist:" + accessToken;
        redisService.save(blacklistKey, "WITHDRAWN", jwtProvider.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);

        user.setStatus(UserStatus.LEAVE);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User deleted: {} (access token blacklisted)", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void forceLogout(String userId, String adminId) {
        // 관리자 권한 확인은 컨트롤러나 필터에서 처리
        // Redis에서 Refresh Token 삭제 (강제 로그아웃)
        String redisKey = "refresh:" + userId;
        redisService.delete(redisKey);

        // AccessToken 차단: blacklist:user:{userId} 저장 (TTL=accessToken 만료 시간)
        String userBlacklistKey = "blacklist:user:" + userId;
        redisService.save(userBlacklistKey, "FORCE_LOGOUT", jwtProvider.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);

        // 로그인 차단: DB is_locked = "Y"
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        user.setIsLocked("Y");
        userRepository.save(user);

        log.info("User force logged out by admin {}: {} (account locked, token blacklisted)", adminId, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void restoreAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.LEAVE) {
            throw new BusinessException(ResponseCode.INVALID_INPUT_VALUE);
        }

        user.setStatus(UserStatus.INACTIVE);
        user.setDeletedAt(null);
        userRepository.save(user);

        log.info("Account restored by admin: {} (LEAVE → INACTIVE)", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reactivateAccount(ReactivateRequest request) {
        // 이메일 인증 확인 (reactivate 용도)
        String verifiedKey = "email:verified:reactivate:" + request.email();
        if (!redisService.hasKey(verifiedKey)) {
            throw new BusinessException(ResponseCode.EMAIL_VERIFICATION_REQUIRED);
        }
        redisService.delete(verifiedKey);

        // INACTIVE 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getStatus() == UserStatus.INACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 계정 활성화
        user.setStatus(UserStatus.ACTIVE);

        // 계정 잠금 해제
        if ("Y".equals(user.getIsLocked())) {
            user.setIsLocked("N");
            user.setLoginFailCount(0);
        }

        userRepository.save(user);
        log.info("Account reactivated: {} (INACTIVE → ACTIVE)", user.getUserId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void changeUserRole(String userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new BusinessException(ResponseCode.INVALID_INPUT_VALUE));

        // 기존 권한 제거
        List<UserRole> existingRoles = userRoleRepository.findByIdUserId(userId);
        userRoleRepository.deleteAll(existingRoles);

        // 새 권한 부여
        UserRole newRole = new UserRole(role.getRoleId(), userId);
        userRoleRepository.save(newRole);

        log.info("User role changed: {} → {}", userId, roleName);
    }

    /**
     * 최근 비밀번호 재사용을 검증합니다.
     * 최근 3회 이내에 사용한 비밀번호와 동일하면 RECENTLY_USED_PASSWORD 예외를 발생시킵니다.
     *
     * @param userId    검증할 사용자 ID
     * @param newPassword 새 비밀번호 (평문)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 최근 사용한 비밀번호와 동일할 시
     */
    private void checkPasswordReuse(String userId, String newPassword) {
        List<PasswordHistory> recentHistories = passwordHistoryRepository.findByUserIdOrderByChangedAtDesc(userId);
        int checkCount = Math.min(recentHistories.size(), 3);
        for (int i = 0; i < checkCount; i++) {
            if (passwordEncoder.matches(newPassword, recentHistories.get(i).getBeforeChangedPwd())) {
                throw new BusinessException(ResponseCode.RECENTLY_USED_PASSWORD);
            }
        }
    }
}
