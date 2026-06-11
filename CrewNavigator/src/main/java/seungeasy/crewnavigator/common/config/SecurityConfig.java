package seungeasy.crewnavigator.common.config;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import seungeasy.crewnavigator.common.reids.RedisService;
import seungeasy.crewnavigator.domain.auth.security.AuthenticationFilter;
import seungeasy.crewnavigator.domain.auth.security.JwtAuthenticationFilter;
import seungeasy.crewnavigator.domain.auth.security.JwtProvider;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;

/**
 * <pre>
 *  Class Name: SecurityConfig
 *  Description: Spring Security 보안 설정
 *  Example:
 *
 *  - JWT 기반 인증 방식 사용
 *  - 비밀번호 암호화 설정
 *
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.11: Seung-Geon: AI를 통해 생성한 내용 검증 진행
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final LoginHistoryRepository loginHistoryRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        // 로그인 필터 생성
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
                authenticationManager, jwtProvider, redisService, objectMapper, loginHistoryRepository);
        authenticationFilter.setFilterProcessesUrl("/auth/login");

        http
                // CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안함 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 스웨거 항상 통과
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 인증 인가 파트
                        .requestMatchers("/auth/signup",
                                "/auth/refresh",
                                "/auth/find-id",
                                "/auth/password/reset",
                                "/auth/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .anyRequest().authenticated()
                )


                // 이 작업은 단순히 값을 설정하는 작업으로
                // 마지막에 build() 메서드가 호출되어야 실제 실행되기 때문에 두 라인의 위치가 변경되어도 상관X
                // 1. JWT 검증 필터: 모든 요청에 대해 토큰 검증
                //  AuthenticationFilter 이전에 jwtAuthenticationFilter를 배치
                .addFilterBefore(jwtAuthenticationFilter, AuthenticationFilter.class)
                // 2. 로그인 필터: UsernamePasswordAuthenticationFilter 위치에 추가5
                //  UsernamePasswordAuthenticationFilter 위치에 authenticationFilter를 배치
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
