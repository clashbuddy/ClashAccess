package studio.clashbuddy.clashaccess.ratelimit;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;


public class RedisRateLimitStorage implements RateLimitStorage {
    private RedisCommands<String, String> commands;
    private StringRedisTemplate redisTemplate;
    private boolean useRedisTemplate = false;
    public RedisRateLimitStorage(String host, int port) {
        commands = createConnection(host, port, "");
    }

    public RedisRateLimitStorage(String host, int port,String password) {
        commands = createConnection(host, port, password);
    }

    public RedisRateLimitStorage(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
        useRedisTemplate=true;
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
        if(useRedisTemplate)
            return redisTemplate.opsForValue().increment(key);
        return commands.incr(key);
    }

    private void expire(String key, long seconds) {
        if(useRedisTemplate)
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        else
            commands.expire(key, seconds);
    }

    @Override
    public int increment(String key, long windowMillis) {
        Long count = increment(key);

        if (count == 1L) {
            expire(key, windowMillis / 1000);
        }

        return count.intValue();
    }

    @Override
    public int currentCount(String key) {
        String value;
        if (useRedisTemplate)
            value = redisTemplate.opsForValue().get(key);
        else
            value = commands.get(key);

        if (value ==null)
            return 0;
        try{
            return Integer.parseInt(value);
        }catch (Exception e){
            return 0;
        }
    }
}