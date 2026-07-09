package seungeasy.crewnavigator.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <pre>
 * Class Name: WebMvcConfig
 * Description: Spring MVC 관련 커스텀 설정을 위한 클래스
 * Example:
 *      // 이 클래스는 Spring Boot 기동 시 자동으로 설정되어 웹 관련 빈을 등록합니다.
 *      // CORS 전역 설정이나, 인터셉터 등록 등의 용도로 사용됩니다.
 *
 * History
 * 2024/06/05  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * CORS(Cross-Origin Resource Sharing) 관련 전역 설정을 정의합니다.
     * 프론트엔드 개발 시, 다른 출처(Origin)의 요청을 허용하기 위해 사용됩니다.
     *
     * @param registry CORS 설정을 위한 CorsRegistry 객체
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO: 프론트엔드 개발 시작 시, 아래 주석을 해제하고 allowedOrigins에 프론트엔드 서버 주소를 추가해야 합니다.
        /*
        registry.addMapping("/**") // 모든 경로에 대해 CORS 정책 적용
                .allowedOrigins("http://localhost:3000", "https://crew-navigator.com") // 요청을 허용할 출처 목록
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 종류의 헤더 허용
                .allowCredentials(true) // 쿠키 등 자격 증명 정보 허용
                .maxAge(3600); // Preflight 요청의 캐시 시간(초)
        */
    }

    /**
     * 특정 경로에 대한 요청을 가로채서 처리하는 인터셉터를 등록합니다.
     * 예: 로그인 인증 인터셉터, 로깅 인터셉터 등
     *
     * @param registry 인터셉터 등록을 위한 InterceptorRegistry 객체
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO: 추후 인터셉터 구현 시, 여기에 등록 로직을 추가해야 합니다.
        // 예: registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/api/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
