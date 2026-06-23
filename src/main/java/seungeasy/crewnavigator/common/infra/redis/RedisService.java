package seungeasy.crewnavigator.common.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *  Class Name: RedisService
 *  Description: Redis 데이터 처리를 위한 서비스 클래스
 *
 *  [주요 기능]
 *  - 데이터 저장, 조회, 삭제
 *  - 키 존재 여부 확인
 *  - 데이터 만료 시간 설정 및 갱신
 *
 *  History
 *  2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 *  2026.06.11: Seung-Geon: Javadoc 주석 추가
 * </pre>
 *
 *  @author seung-geon
 * @version 1.1
 */
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis에 데이터를 저장합니다. (만료 시간 설정 가능)
     *
     * @param key 저장할 데이터의 키
     * @param value 저장할 데이터
     * @param timeout 만료 시간
     * @param unit 시간 단위
     */
    public void save(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * Redis에서 데이터를 조회합니다.
     *
     * @param key 조회할 데이터의 키
     * @return 조회된 데이터 (없을 경우 null)
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Redis에서 데이터를 삭제합니다.
     *
     * @param key 삭제할 데이터의 키
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Redis에 해당 키가 존재하는지 확인합니다.
     *
     * @param key 확인할 키
     * @return 키 존재 여부 (true/false)
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Redis에서 패턴에 일치하는 모든 키를 조회합니다.
     * <p>
     * <b>운영 환경 주의:</b> 이 메서드는 내부적으로 {@code KEYS} 명령을 사용하므로
     * 키 개수가 많을 경우 성능에 영향을 줄 수 있습니다.
     * 실제 운영 환경에서는 {@code SCAN} 명령 사용을 권장합니다.
     *
     * @param pattern 키 패턴 (예: "refresh:*")
     * @return 일치하는 키의 Set (없으면 빈 Set)
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * Redis에 저장된 데이터의 만료 시간을 갱신합니다.
     *
     * @param key 대상 키
     * @param timeout 설정할 만료 시간
     * @param unit 시간 단위
     * @return 만료 시간 설정 성공 여부
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }
}
