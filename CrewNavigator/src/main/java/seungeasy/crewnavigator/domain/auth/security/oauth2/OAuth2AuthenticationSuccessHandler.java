package seungeasy.crewnavigator.domain.auth.security.oauth2;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import seungeasy.crewnavigator.common.infra.redis.RedisService;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;
import seungeasy.crewnavigator.domain.auth.security.JwtProvider;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *  Class Name: OAuth2AuthenticationSuccessHandler
 *  Description: OAuth2 로그인 성공 시 JWT를 발급하고 응답을 처리하는 핸들러.
 *               기존 form 로그인(AuthenticationFilter.successfulAuthentication)과 동일한
 *               토큰 발급 로직을 따릅니다.
 *
 *  [처리 흐름]
 *  1. SecurityContext에서 인증된 사용자 정보 조회
 *  2. Access Token / Refresh Token 생성
 *  3. Refresh Token Redis에 저장
 *  4. Access Token은 Body, Refresh Token은 HttpOnly Cookie로 전송
 *
 * History
 * 2026.07.04: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String userId = authentication.getName();

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        // Refresh Token Redis 저장
        String redisKey = "refresh:" + userId;
        redisService.save(redisKey, refreshToken, jwtProvider.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        log.info("OAuth2 login success: {}", userId);

        // 응답 생성 (Access Token만 body에 전송)
        TokenResponse tokenResponse = TokenResponse.of(accessToken, null, jwtProvider.getAccessTokenExpiration() / 1000);
        CustomResponse<TokenResponse> customResponse = CustomResponse.success(ResponseCode.OK, tokenResponse);

        // Refresh Token은 HttpOnly Cookie로 전송 (XSS 방지)
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofMillis(jwtProvider.getRefreshTokenExpiration()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
    }
}
