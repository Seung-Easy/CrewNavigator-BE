package seungeasy.crewnavigator.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * Class Name: SwaggerConfig
 * Description: SpringDoc OpenAPI (Swagger UI) 설정을 위한 클래스
 * Example:
 *      // 이 클래스의 설정으로 인해 서버 기동 시 자동으로 API 문서가 생성됩니다.
 *      // 브라우저에서 아래 주소로 접속하여 API 명세서를 확인할 수 있습니다.
 *      // http://localhost:5000/swagger-ui/index.html
 *
 * History
 * 2024/06/05  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "CrewNavigator REST API 명세서",
                description = "CrewNavigator REST API를 설명하기 위한 명세서",
                version = "v1.0.0"
        )
)
@Configuration
public class SwaggerConfig {
}
