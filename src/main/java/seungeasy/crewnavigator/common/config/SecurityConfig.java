package seungeasy.crewnavigator.common.config;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import seungeasy.crewnavigator.common.infra.redis.RedisService;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.security.AuthenticationFilter;
import seungeasy.crewnavigator.domain.auth.security.JwtAuthenticationFilter;
import seungeasy.crewnavigator.domain.auth.security.JwtProvider;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;

/**
 * <pre>
 *  Class Name: SecurityConfig
 *  Description: Spring Security 보안 설정
 *  
 *  [주요 설정 내용]
 *  - CSRF 보호 비활성화 (REST API)
 *  - 세션 관리 상태를 Stateless로 설정 (JWT 사용)
 *  - URL별 접근 권한 제어 (Swagger, Auth 관련 API 등은 허용, Admin API 인증 필요)
 *  - JWT 검증 및 커스텀 로그인 필터 적용
 *  - 비밀번호 암호화 (BCryptPasswordEncoder)
 *
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.11: Seung-Geon: AI를 통해 생성한 내용 검증 진행
 * 2026.06.12: Seung-Geon: AuthenticationEntryPoint fallback 추가, UserRepository 주입
 * 2026.06.15: Seung-Geon: /admin/auth/** admin 전용 API 경로 추가
 * 2026.06.16: Seung-Geon: RoleHierarchy 빈 등록 (ADMIN > MANAGER > OPERATOR > USER)
 * 2026.06.16: Seung-Geon: @EnableMethodSecurity 추가, /admin/** → .authenticated() (메서드 수준 제어로 위임)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.2
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;

    /**
     * AuthenticationManager 빈을 등록합니다.
     * Spring Security의 인증 관리자로 UsernamePasswordAuthenticationToken을 처리합니다.
     *
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager 인스턴스
     * @throws Exception 설정 처리 중 예외 발생 시
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain 빈을 등록합니다. HTTP 보안 설정을 정의합니다.
     *
     * <pre>
     * 설정 내용:
     * - CSRF 비활성화 (REST API)
     * - 세션 Stateless (JWT 사용)
     * - URL별 접근 권한 제어
     * - JWT 검증 필터(JwtAuthenticationFilter)와 로그인 필터(AuthenticationFilter) 적용
     * - AuthenticationEntryPoint로 인증 실패 시 JSON 응답
     * </pre>
     *
     * @param http                  HttpSecurity
     * @param authenticationManager AuthenticationManager
     * @return 설정이 완료된 SecurityFilterChain
     * @throws Exception 설정 처리 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        // 로그인 필터 생성
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
                authenticationManager, jwtProvider, redisService, objectMapper, loginHistoryRepository, userRepository);
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

                        // Spring Boot Actuator 기능을 제공하는 엔드포인트
                        //  애플리케이션의 상태를 모니터링하고 관리하기 위한 관리자용 페이지
                        //  주요 기능
                        //      actuator/health: 애플리케이션이 정상적으로 실행 중인지 상태를 보여줌
                        //      actuator/info: 애플리케이션의 기본 정보를 보여줌 (버전, 이름 등)
                        //      actuator/metrics: CPU 사용량, 메모리 사용량, HTTP 요청 수 등 다양한 성능 지표 제공
                        //      actuator/beans: Spring 컨테이너에 등록된 모든 빈(Bean)의 목록을 보여줌
                        //      actuator/mappings: 어떤 URL이 어떤 컨트롤러 메서드와 매핑되어 있는지 전체 목록을 보여줌
                        //      actuator/env: 시스템 환경 변수, 설정 파일(application.yml)의 속성 등 애플리케이션이 사용하는 모든 환경 정보를 보여줌
                        //  주의 사항
                        //      개발 환경에서는 permitAll()이 편리하지만 운영 환경에서는 매우 위함
                        //      엔드포인트를 통해 데이터베이스 접속 정보 등 민감한 정보가 유츌될 수 있음
                        //      운영 환경으로 배포전 반드시 hasRole("ADMIN")과 같이 제한을 해줘야 함
                        .requestMatchers("/actuator/**").permitAll()

                        // 인증 인가 파트
                        .requestMatchers("/auth/signup",
                                "/auth/email/**",
                                "/auth/refresh",
                                "/auth/find-id",
                                "/auth/password/reset",
                                "/auth/account/reactivate",
                                "/auth/health").permitAll()



                        // 관리자 API (RoleHierarchy + @PreAuthorize로 세부 제어)
                        .requestMatchers("/admin/**").authenticated()

                        .anyRequest().authenticated()
                )

                // 인증 실패 시 JSON 응답 (토큰이 아예 없는 경우)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(ResponseCode.UNAUTHORIZED_ACCESS.getHttpStatus().value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(objectMapper.writeValueAsString(
                                    CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS)
                            ));
                        })
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

    /**
     * PasswordEncoder 빈을 등록합니다. BCrypt 해싱 알고리즘을 사용합니다.
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * RoleHierarchy 빈을 등록합니다.
     * 상위 권한이 하위 권한의 접근 권한을 자동으로 상속받습니다.
     * {@code @PreAuthorize("hasRole('...')")} 와 함께 사용되어
     * ADMIN > MANAGER > OPERATOR > USER 순으로 계층이 적용됩니다.
     *
     * @return RoleHierarchy 인스턴스
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER \n ROLE_MANAGER > ROLE_OPERATOR \n ROLE_OPERATOR > ROLE_USER");
    }
}