package studio.clashbuddy.clashaccess.otp;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationSessionService {

    private final RedisTemplate<String, Object> redisTemplate;


    private static final String SESSION_PREFIX = "otp-session:";

    public VerificationSessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public String createSession(VerificationSessionDTO dto, long ttl, TimeUnit unit) {
        String sessionId = UUID.randomUUID().toString();
        String key = buildKey(sessionId); // key = "otp-session:" + UUID
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();

        ops.put(key, "userId", dto.getUserId());
        ops.put(key, "cbPayId", dto.getCbPayId());
        ops.put(key, "reason", dto.getReason());
        ops.put(key, "metadata", dto.getMetadata());

        redisTemplate.expire(key, ttl, unit);
        return sessionId;
    }

    public VerificationSessionDTO getSession(String sessionId) {
        String key = buildKey(sessionId);

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return null;
        }

        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();

        List<Object> values = ops.multiGet(
                key,
                List.of("userId", "cbPayId", "reason", "metadata")
        );

        if (values == null || values.get(0) == null) {
            return null;
        }

        return new VerificationSessionDTO(
                (String) values.get(0),
                (String) values.get(1),
                (String) values.get(2),
                values.get(3)
        );
    }

    public void deleteSession(String sessionId) {
        redisTemplate.delete(buildKey(sessionId));
    }

    public boolean exists(String userId, String cbPayId, String reason) {
        String key = buildSessionKey(userId, cbPayId, reason);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String buildKey(String sessionId) {
        return SESSION_PREFIX + sessionId;
    }

    private String buildSessionKey(String userId, String cbPayId, String reason) {
        return SESSION_PREFIX + userId + ":" + cbPayId + ":" + reason;
    }
}