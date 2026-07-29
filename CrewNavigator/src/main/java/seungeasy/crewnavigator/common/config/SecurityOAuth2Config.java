package seungeasy.crewnavigator.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import seungeasy.crewnavigator.domain.auth.security.oauth2.OAuth2AuthenticationFailureHandler;
import seungeasy.crewnavigator.domain.auth.security.oauth2.OAuth2AuthenticationSuccessHandler;
import seungeasy.crewnavigator.domain.auth.security.oauth2.PrincipalOAuth2UserService;

/**
 * <pre>
 *  Class Name: SecurityOAuth2Config
 *  Description: OAuth2 로그인 전용 보안 설정.
 *              SecurityConfig와의 순환 참조를 방지하기 위해 별도 설정 클래스로 분리했습니다.
 *
 *  [주요 설정]
 *  - OAuth2 로그인 엔드포인트 설정
 *  - securityMatcher로 /login/**, /oauth2/** 경로만 처리
 *
 * History
 * 2026.07.04: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityOAuth2Config {

    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    /**
     * OAuth2 로그인 전용 SecurityFilterChain.
     * SecurityConfig의 filterChain보다 우선 적용(@Order(1))됩니다.
     * /login/** , /oauth2/** 경로의 요청만 처리합니다.
     *      요청 URL: /oauth2/authorization/{registrationId}
     *
     *
     *
     * @param http HttpSecurity
     * @return OAuth2 설정이 적용된 SecurityFilterChain
     * @throws Exception 설정 처리 중 예외 발생 시
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login/**", "/oauth2/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        return http.build();
    }
}
