package seungeasy.crewnavigator.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/**
 * <pre>
 * Class Name: AppConfig
 * Description: 애플리케이션 전반에서 사용되는 공통 빈(Bean)을 등록하기 위한 설정 클래스
 * Example:
 *      // 이 클래스는 Spring Boot 기동 시 자동으로 설정되어 공통 빈을 등록합니다.
 *      // 서비스 계층 등에서 ModelMapper를 주입받아 사용할 수 있습니다.
 *      // @Autowired
 *      // private ModelMapper modelMapper;
 *
 * History
 * 2024/06/05  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Configuration
public class AppConfig {

    /**
     * DTO와 Entity 간의 필드 값 매핑을 용이하게 하기 위한 ModelMapper 빈을 등록합니다.
     *
     * @return ModelMapper 인스턴스
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * JSON과 Java 객체 간의 변환을 담당하는 ObjectMapper 빈을 등록합니다.
     * Spring Boot가 기본적으로 등록해주지만, 커스텀 설정을 위해 직접 빈으로 등록합니다.
     *
     * @return 커스텀 설정이 적용된 ObjectMapper 인스턴스
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
