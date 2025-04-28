package studio.clashbuddy.clashaccess.ratelimit;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;


public class RedisRateLimitStorage implements RateLimitStorage {
    private final RedisCommands<String, String> commands;


    public RedisRateLimitStorage(String host, int port,String password) {
        commands = createConnection(host, port, password);
    }

    private RedisCommands<String, String> createConnection(String host, int port, String password) {
        try (RedisClient client = RedisClient.create("redis://%s:%d".formatted(host,port))) {
            try (StatefulRedisConnection<String, String> connection = client.connect()) {
                return connection.sync();
            }
        }
    }

    private Long increment(String key) {
        return commands.incr(key);
    }

    private void expire(String key, long seconds) {
        commands.expire(key, seconds);
    }

    @Override
    public int incrementAndGet(String key, long windowMillis) {
        Long count = increment(key);

        if (count == 1L) {
            expire(key, windowMillis / 1000);
        }

        return count.intValue();
    }
}