package newsugar.Newsugar_Back.domain.summary.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CategorySummaryRedis {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.ofHours(6); // 6시간 TTL

    public CategorySummaryRedis(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Redis에 카테고리 요약 저장
    public void saveSummary(String key, String summary) {
        redisTemplate.opsForValue().set(key, summary, TTL);
    }

    // Redis에서 카테고리 요약 조회
    public String getSummary(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Redis에서 삭제
    public void deleteSummary(String key) {
        redisTemplate.delete(key);
    }
}