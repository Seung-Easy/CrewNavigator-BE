package seungeasy.crewnavigator.common.reids;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class  RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 토큰 저장 (만료 시간 설정)
    public void save(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 토큰 조회
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 토큰 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 키 존재 여부 확인
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 만료 시간 갱신
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }
}
