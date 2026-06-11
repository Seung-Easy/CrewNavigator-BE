package seungeasy.crewnavigator.domain.auth.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import seungeasy.crewnavigator.common.reids.RedisService;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.LoginRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;
import seungeasy.crewnavigator.domain.auth.entity.LoginHistory;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final LoginHistoryRepository loginHistoryRepository;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                JwtProvider jwtProvider,
                                RedisService redisService,
                                ObjectMapper objectMapper,
                                LoginHistoryRepository loginHistoryRepository) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                  HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.userId(), loginRequest.password());

            return this.getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse login request", e);
        }
    }

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

        // 로그인 이력 저장
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setLoginAt(LocalDateTime.now());
        loginHistoryRepository.save(history);

        log.info("User logged in: {}", userId);

        // 응답 생성
        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtProvider.getAccessTokenExpiration() / 1000);
        CustomResponse<TokenResponse> customResponse = CustomResponse.success(ResponseCode.OK, tokenResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.warn("Login failed: {}", failed.getMessage());

        ResponseCode errorCode;
        if (failed instanceof LockedException) {
            errorCode = ResponseCode.ACCOUNT_LOCKED;
        } else if (failed instanceof DisabledException) {
            errorCode = ResponseCode.ACCOUNT_INACTIVE;
        } else {
            errorCode = ResponseCode.INVALID_PASSWORD;
        }

        CustomResponse<Void> errorResponse = CustomResponse.error(errorCode);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
