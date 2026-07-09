package seungeasy.crewnavigator.domain.auth.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import seungeasy.crewnavigator.common.infra.redis.RedisService;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.LoginRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;
import seungeasy.crewnavigator.domain.auth.entity.LoginHistory;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *  Class Name: AuthenticationFilter
 *  Description: 사용자 로그인 인증을 처리하는 커스텀 필터.
 *
 *  [주요 기능]
 *  - `/auth/login` 경로의 POST 요청을 가로채 사용자 인증을 시도합니다.
 *  - 인증 성공 시, JWT(Access Token, Refresh Token)를 생성하고 응답으로 반환합니다.
 *  - Refresh Token은 Redis에 저장하여 관리합니다.
 *  - 로그인 성공 이력을 데이터베이스에 기록합니다.
 *  - 인증 실패 시, 실패 유형에 맞는 에러 응답을 반환합니다.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: MAX_LOGIN_ATTEMPTS 계정 잠금 로직 추가, 예외 타입별 에러 코드 세분화
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // 최대 로그인 실패 횟수 (초과 시 계정 잠금)
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                JwtProvider jwtProvider,
                                RedisService redisService,
                                ObjectMapper objectMapper,
                                LoginHistoryRepository loginHistoryRepository,
                                UserRepository userRepository) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.loginHistoryRepository = loginHistoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * 사용자 인증을 시도합니다.
     * HTTP 요청에서 사용자 ID와 비밀번호를 추출하여 `UsernamePasswordAuthenticationToken`을 생성하고,
     * `AuthenticationManager`에 인증을 위임합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @return 인증된 사용자의 Authentication 객체
     * @throws AuthenticationException 인증 실패 시 발생
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                  HttpServletResponse response) throws AuthenticationException {
        try {
            // 1. 입력으로 들어온 값을 DTO에 맞춰서 매핑
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            // 2. 실패 시에도 userId를 알 수 있도록 request에 저장
            request.setAttribute("login_user_id", loginRequest.userId());

            // 3. 인증용 토큰 생성
            log.info("로그인 시도: {}", loginRequest.userId());
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.userId(), loginRequest.password());

            // 3. AuthenticationManager에 토큰을 전달하여 인증 시도
            //      여기서 발생하는 예외도 전부 AuthenticationException이기 때문에 catch를 하지 않아도 시그니처로 처리
            return this.getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            // 현재 메서드에서 발생할 수 있는 IOException은 1번의 매핑 단계에서만 발생 가능
            log.error("로그인 요청 처리 중 오류 발생", e);
            throw new AuthenticationServiceException("Failed to parse login request", e);
        }
    }

    /**
     * 인증 성공 후 호출됩니다.
     * Access Token과 Refresh Token을 생성하고, Refresh Token은 Redis에 저장합니다.
     * 로그인 성공 이력을 기록한 후, 클라이언트에게 토큰 정보를 응답으로 전송합니다.
     *
     * @param request    HttpServletRequest 객체
     * @param response   HttpServletResponse 객체
     * @param chain      FilterChain 객체
     * @param authResult 인증 결과 정보를 담은 Authentication 객체
     * @throws IOException 응답 작성 중 오류 발생 시
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        String userId = authResult.getName();

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        // Refresh Token Redis 저장
        String redisKey = "refresh:" + userId;
        redisService.save(redisKey, refreshToken, jwtProvider.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        // 로그인 이력 저장 (성공 — 실패해도 응답은 정상 전송)
        try {
            LoginHistory history = new LoginHistory();
            history.setUserId(userId);
            history.setLoginAt(LocalDateTime.now());
            history.setIpAddress(request.getRemoteAddr());
            history.setIsActivated(true);
            loginHistoryRepository.save(history);
        } catch (Exception e) {
            log.error("로그인 성공 이력 저장 중 오류 발생", e);
        }

        // 로그인 성공 시 실패 횟수 초기화
        try {
            userRepository.findById(userId).ifPresent(user -> {
                if (user.getLoginFailCount() != null && user.getLoginFailCount() > 0) {
                    user.setLoginFailCount(0);
                    userRepository.save(user);
                }
            });
        } catch (Exception e) {
            log.error("로그인 실패 횟수 초기화 중 오류 발생", e);
        }

        log.info("User logged in: {}", userId);

        // 응답 생성 (Access Token만 body에 전송)
        TokenResponse tokenResponse = TokenResponse.of(accessToken, null, jwtProvider.getAccessTokenExpiration() / 1000);
        CustomResponse<TokenResponse> customResponse = CustomResponse.success(ResponseCode.OK, tokenResponse);

        // Refresh Token은 HttpOnly Cookie로 전송 (XSS 방지)
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")      // /auth/refresh 요청에만 해당 쿠키를 전송
                .maxAge(Duration.ofMillis(jwtProvider.getRefreshTokenExpiration()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
    }

    /**
     * 인증 실패 후 호출됩니다.
     * 실패 원인(계정 잠김, 비활성화, 비밀번호 오류 등)에 따라 적절한 에러 코드를 설정하고,
     * 클라이언트에게 에러 정보를 응답으로 전송합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param failed   발생한 AuthenticationException 객체
     * @throws IOException 응답 작성 중 오류 발생 시
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.warn("Login failed: {}", failed.getMessage());

        // 로그인 실패 이력 저장 (실패해도 응답은 정상 전송)
        try {
            String userId = (String) request.getAttribute("login_user_id");
            LoginHistory history = new LoginHistory();
            history.setUserId(userId != null ? userId : "unknown");
            history.setLoginAt(LocalDateTime.now());
            history.setIpAddress(request.getRemoteAddr());
            history.setIsActivated(false);
            loginHistoryRepository.save(history);
        } catch (Exception e) {
            log.error("로그인 실패 이력 저장 중 오류 발생", e);
        }

        // 실패 횟수 증가 및 계정 잠금 (BadCredentialsException인 경우만)
        // LockedException 등은 이미 계정이 잠긴 상태이므로 카운트하지 않음
        if (failed instanceof BadCredentialsException || failed instanceof AuthenticationServiceException) {
            try {
                String userId = (String) request.getAttribute("login_user_id");
                if (userId != null) {
                    userRepository.findById(userId).ifPresent(user -> {
                        int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
                        user.setLoginFailCount(failCount);
                        if (failCount >= MAX_LOGIN_ATTEMPTS) {
                            user.setIsLocked("Y");
                            log.warn("계정이 잠겼습니다: {} ({}회 실패)", userId, failCount);
                        }
                        userRepository.save(user);
                    });
                }
            } catch (Exception e) {
                log.error("로그인 실패 횟수 증가 중 오류 발생", e);
            }
        }

        ResponseCode errorCode;
        if (failed instanceof LockedException) {
            errorCode = ResponseCode.ACCOUNT_LOCKED;
        } else if (failed instanceof DisabledException) {
            errorCode = ResponseCode.ACCOUNT_INACTIVE;
        } else if (failed instanceof AccountExpiredException) {
            errorCode = ResponseCode.ACCOUNT_EXPIRED;
        } else if (failed instanceof CredentialsExpiredException) {
            errorCode = ResponseCode.PASSWORD_EXPIRED;
        } else {
            errorCode = ResponseCode.INVALID_CREDENTIALS;
        }

        CustomResponse<Void> errorResponse = CustomResponse.error(errorCode);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
