package studio.clashbuddy.clashaccess.ratelimit;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class RedisRateLimitStorage implements RateLimitStorage {
    private RedisCommands<String, String> commands;
    private StringRedisTemplate redisTemplate;
    private boolean useRedisTemplate = false;
    private static final RedisScript<Long> FIXED_WINDOW_SCRIPT = new DefaultRedisScript<>(
            """
                    local count = redis.call("INCR", KEYS[1])
                    if count == 1 then
                        redis.call("PEXPIRE", KEYS[1], ARGV[1])
                    end
                    return count
                    """,
            Long.class
    );

    private static final RedisScript<Long> SLIDING_WINDOW_SCRIPT = new DefaultRedisScript<>(
            """
                    local count = redis.call("INCR", KEYS[1])
                    redis.call("PEXPIRE", KEYS[1], ARGV[1])
                    return count
                    """,
            Long.class
    );

    private static final RedisScript<Long> TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>(
            """
                    -- KEYS[1] = key
                    -- ARGV[1] = capacity
                    -- ARGV[2] = refillTokensPerMillis
                    -- ARGV[3] = cost
                    -- ARGV[4] = nowMillis
                    
                    local tokens = redis.call("HGET", KEYS[1], "tokens")
                    local lastRefillAt = redis.call("HGET", KEYS[1], "lastRefillAt")
                    
                    tokens = tonumber(tokens)
                    lastRefillAt = tonumber(lastRefillAt)
                    
                    if tokens == nil then
                        tokens = tonumber(ARGV[1])
                        lastRefillAt = tonumber(ARGV[4])
                    else
                        local elapsed = tonumber(ARGV[4]) - lastRefillAt
                        local refill = elapsed * tonumber(ARGV[2])
                        tokens = math.min(tonumber(ARGV[1]), tokens + refill)
                        lastRefillAt = tonumber(ARGV[4])
                    end
                    
                    if tokens < tonumber(ARGV[3]) then
                        redis.call("HSET", KEYS[1], "tokens", tokens, "lastRefillAt", lastRefillAt)
                        return -1
                    end
                    
                    tokens = tokens - tonumber(ARGV[3])
                    
                    redis.call("HSET", KEYS[1], "tokens", tokens, "lastRefillAt", lastRefillAt)
                    
                    -- FIX: force TTL to integer
                    local ttlMillis = math.floor(tonumber(ARGV[1]) / tonumber(ARGV[2]))
                    redis.call("PEXPIRE", KEYS[1], ttlMillis)
                    
                    return math.floor(tokens)
                    """,
            Long.class
    );


    public RedisRateLimitStorage(String host, int port) {
        commands = createConnection(host, port, "");
    }

    public RedisRateLimitStorage(String host, int port, String password) {
        commands = createConnection(host, port, password);
    }

    public RedisRateLimitStorage(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
        useRedisTemplate = true;
    }

    private RedisCommands<String, String> createConnection(String host, int port, String password) {
        final RedisCommands<String, String> commands;
        String uri = (password != null && !password.isEmpty())
                ? "redis://:" + password + "@" + host + ":" + port
                : "redis://" + host + ":" + port;

        RedisClient client = RedisClient.create(uri);
        StatefulRedisConnection<String, String> connection = client.connect(); // hold open connection
        return connection.sync();
    }


    private Long increment(String key) {
        if (useRedisTemplate)
            return redisTemplate.opsForValue().increment(key);
        return commands.incr(key);
    }

    private void expire(String key, long seconds) {
        if (useRedisTemplate)
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        else
            commands.expire(key, seconds);
    }

    @Override
    public int increment(String key, long windowMillis, double refillTokensPerMillis, int cost, RateLimitWindowType rateLimitWindowType) {
        if (rateLimitWindowType.equals(RateLimitWindowType.SLIDING))
            return incrementForSliding(key, windowMillis);
        else if (rateLimitWindowType.equals(RateLimitWindowType.TOKEN_BUCKET))
            return incrementForTokeBucket(key, windowMillis, refillTokensPerMillis, cost);
        return incrementForFixed(key, windowMillis);
    }

    private int incrementForTokeBucket(String key, long windowMillis, double refillTokensPerMillis, int cost) {
        double capacity = refillTokensPerMillis * windowMillis;
        long now = System.currentTimeMillis();
        Long result = redisTemplate.execute(TOKEN_BUCKET_SCRIPT, Collections.singletonList(key), String.valueOf(capacity),
                String.valueOf(refillTokensPerMillis), String.valueOf(cost), String.valueOf(now)
        );

        return result == null ? -1 : result.intValue();
    }

    private int incrementForSliding(String key, long windowMillis) {
        Long result = redisTemplate.execute(SLIDING_WINDOW_SCRIPT, Collections.singletonList(key), String.valueOf(windowMillis));
        return result == null ? 0 : result.intValue();
    }

    private int incrementForFixed(String key, long windowMillis) {
        Long result = redisTemplate.execute(FIXED_WINDOW_SCRIPT, Collections.singletonList(key), String.valueOf(windowMillis));
        return result == null ? 0 : result.intValue();
    }

}