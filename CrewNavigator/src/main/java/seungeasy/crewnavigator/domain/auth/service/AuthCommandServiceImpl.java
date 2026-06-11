package seungeasy.crewnavigator.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.reids.RedisService;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.*;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;
import seungeasy.crewnavigator.domain.auth.entity.*;
import seungeasy.crewnavigator.domain.auth.repository.*;
import seungeasy.crewnavigator.domain.auth.security.JwtProvider;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Override
    @Transactional
    public void signup(SignupRequest request) {
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
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setBirthday(request.birthday() != null ? java.time.LocalDate.parse(request.birthday()) : null);
        user.setAddress(request.address());

        userRepository.save(user);
        log.info("User signed up: {}", request.userId());
    }

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

    @Override
    @Transactional
    public void changePassword(String userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.INVALID_PASSWORD);
        }

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

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getUserId().equals(request.userId()))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password reset for user: {}", request.userId());
    }

    @Override
    @Transactional
    public void deleteAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // Refresh Token 삭제
        String redisKey = "refresh:" + userId;
        redisService.delete(redisKey);

        user.setStatus(UserStatus.LEAVE);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User deleted: {}", userId);
    }

    @Override
    @Transactional
    public void forceLogout(String userId, String adminId) {
        // 관리자 권한 확인은 컨트롤러나 필터에서 처리
        // Redis에서 Refresh Token 삭제 (강제 로그아웃)
        String redisKey = "refresh:" + userId;
        redisService.delete(redisKey);

        log.info("User force logged out by admin {}: {}", adminId, userId);
    }
}
