package seungeasy.crewnavigator.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * <pre>
 * Class Name: MyBatisConfig
 * Description: MyBatis 프레임워크 설정 및 Mapper 스캔을 위한 설정 클래스
 * Example:
 *      // 이 클래스는 Spring Boot 기동 시 자동으로 설정되어 MyBatis 빈을 등록합니다.
 *      // application.yml에 DataSource(DB 연결) 설정이 선행되어야 합니다.
 *      // @MapperScan을 통해 지정된 패키지 하위의 인터페이스를 Mapper 빈으로 자동 등록합니다.
 *
 * History
 * 2024/06/05  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Configuration
@MapperScan(basePackages = "seungeasy.crewnavigator.domain.**.mapper")
public class MyBatisConfig {
    
    /**
     * SqlSessionFactory 빈 생성
     * MyBatis의 핵심 설정 및 Mapper XML 위치 설정
     *
     * @param dataSource 데이터베이스 연결 정보 (Spring Boot가 자동 주입)
     * @return 설정이 완료된 SqlSessionFactory
     * @throws Exception Mapper 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // MyBatis 동작 옵션 설정
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();

        // DB 컬럼명(snake_case) → 자바 필드명(camelCase) 자동 변환
        // 예시: user_name → userName
        configuration.setMapUnderscoreToCamelCase(true);

        // null 값도 setter 메서드 호출 (기본값은 호출 안 함)
        configuration.setCallSettersOnNulls(true);

        // null 값을 DB에 저장할 때 JDBC 타입 지정
        configuration.setJdbcTypeForNull(org.apache.ibatis.type.JdbcType.NULL);

        sessionFactory.setConfiguration(configuration);

        // Mapper XML 파일 위치 설정
        // classpath:mapper/**/*.xml 패턴으로 모든 Mapper XML 파일 자동 로드
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(
                resolver.getResources("classpath:mapper/**/*.xml")
        );

        // Type Alias 패키지 설정 (XML에서 풀 패키지명 대신 클래스명만 사용 가능)
        // 예: seungeasy.crewnavigator.domain.user.dto.UserDto → UserDto
        sessionFactory.setTypeAliasesPackage("seungeasy.crewnavigator.domain");

        return sessionFactory.getObject();
    }
}
