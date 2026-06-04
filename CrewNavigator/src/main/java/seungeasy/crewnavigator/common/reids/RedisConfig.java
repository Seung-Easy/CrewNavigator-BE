package seungeasy.crewnavigator.common.reids;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * <pre>
 * Class Name: RedisConfig
 * Description: Redis 서버 연결 및 RedisTemplate 설정을 위한 설정 클래스
 * Example:
 *      // 이 클래스는 Spring Boot 기동 시 자동으로 설정되어 빈을 등록합니다.
 *      // application.yml에 다음과 같이 설정해야 합니다.
 *      // spring:
 *      //   data:
 *      //     redis:
 *      //       host: localhost
 *      //       port: 6379
 *
 * History
 * 2024/06/05  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * Redis 서버와의 연결을 관리하는 ConnectionFactory 빈을 생성합니다.
     * 고성능 논블로킹 클라이언트인 Lettuce를 사용합니다.
     *
     * @return RedisConnectionFactory 인스턴스
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * Redis 데이터 접근을 위한 RedisTemplate 빈을 생성합니다.
     * 데이터를 저장할 때 사람이 읽을 수 있는 형태로 저장하기 위해 직렬화(Serializer) 설정을 추가했습니다.
     *
     * @return 직렬화 설정이 완료된 RedisTemplate 인스턴스
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key는 일반 문자열로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JacksonJsonRedisSerializer<>(Object.class));

        // Hash를 사용할 경우 시리얼라이저 설정
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JacksonJsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }
}
