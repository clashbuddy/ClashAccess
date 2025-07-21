package studio.clashbuddy.clashaccess.otp;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

        redisTemplate.expire(key, ttl, unit);
        return sessionId;
    }

    public VerificationSessionDTO getSession(String sessionId) {
        String key = buildKey(sessionId);
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return null;
        }

        String userId =  (String) ops.get(key, "userId");
        String  cbPayId= (String) ops.get(key, "cbPayId");
        String reason = (String) ops.get(key, "reason");
        return new VerificationSessionDTO(userId,cbPayId,reason);
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