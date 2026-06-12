package seungeasy.crewnavigator.domain.auth.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import seungeasy.crewnavigator.common.reids.RedisService;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;

import java.io.IOException;

/**
 * <pre>
 *  Class Name: JwtAuthenticationFilter
 *  Description: 모든 HTTP 요청에 대해 JWT Access Token을 검증하는 필터.
 *
 *  [주요 기능]
 *  - 요청 헤더에서 Bearer 토큰 추출
 *  - 토큰 상태 확인 (만료/위조/블랙리스트) 후 상태별 JSON 에러 응답
 *  - 정상 토큰인 경우 SecurityContext에 인증 정보 설정
 *  - AuthenticationFilter 이전에 실행되어 모든 요청의 토큰을 사전 검증
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: 토큰 상태별 응답 로직 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    /**
     * 모든 HTTP 요청에 대해 JWT 토큰을 검증하고 SecurityContext를 설정합니다.
     *
     * <pre>
     * 처리 순서:
     * 1. 요청 헤더에서 Bearer 토큰 추출
     * 2. 토큰이 없으면 다음 필터로 통과 (SecurityConfig의 .authenticated() fallback)
     * 3. 토큰 상태 확인 (만료 → EA006 / 위조 → EA005 / 블랙리스트 → EA007)
     * 4. 정상 토큰이면 SecurityContext에 인증 정보 설정 후 다음 필터로 통과
     * </pre>
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException 서블릿 예외
     * @throws IOException I/O 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        // 토큰이 없으면 통과 (SecurityConfig의 .authenticated()에서 처리)
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 상태 확인 (만료 vs 위조)
        JwtProvider.TokenStatus status = jwtProvider.getTokenStatus(token);
        if (status == JwtProvider.TokenStatus.EXPIRED) {
            log.warn("Expired token: {}", token);
            writeErrorResponse(response, ResponseCode.EXPIRED_TOKEN);
            return;
        }
        if (status == JwtProvider.TokenStatus.INVALID) {
            log.warn("Invalid token: {}", token);
            writeErrorResponse(response, ResponseCode.INVALID_TOKEN);
            return;
        }

        // 블랙리스트 확인 (로그아웃/강제 로그아웃)
        String blacklistKey = "blacklist:" + token;
        if (redisService.hasKey(blacklistKey)) {
            log.warn("Blacklisted token detected: {}", token);
            writeErrorResponse(response, ResponseCode.TOKEN_BLACKLISTED);
            return;
        }

        // 정상 토큰 → SecurityContext에 인증 정보 설정
        String userId = jwtProvider.getUserIdFromToken(token);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * JSON 형식의 에러 응답을 HttpServletResponse에 작성합니다.
     *
     * @param response     HttpServletResponse
     * @param responseCode 응답에 사용할 ResponseCode (HTTP 상태, 커스텀 코드, 메시지 포함)
     * @throws IOException 응답 작성 중 오류 발생 시
     */
    private void writeErrorResponse(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        CustomResponse<Void> errorResponse = CustomResponse.error(responseCode);
        response.setStatus(responseCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * HTTP 요청 헤더에서 Bearer 토큰을 추출합니다.
     *
     * @param request HttpServletRequest
     * @return 추출된 토큰 문자열 (Bearer 접두사 제거), 없으면 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
