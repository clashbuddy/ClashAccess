package studio.clashbuddy.clashaccess.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRateLimitStorage implements RateLimitStorage {

    private InMemoryRateLimitStorage() {
    }

    private final static InMemoryRateLimitStorage INSTANCE = new InMemoryRateLimitStorage();

    public static InMemoryRateLimitStorage instance() {
        return INSTANCE;
    }


    private static class Counter {
        private int count;
        private long expireAt;

        Counter(int count, long expireAt) {
            this.count = count;
            this.expireAt = expireAt;
        }
    }

    private static class TokenBucket {
        double tokens;
        long lastRefillAt;

        TokenBucket(double tokens, long lastRefillAt) {
            this.tokens = tokens;
            this.lastRefillAt = lastRefillAt;
        }
    }

    private final Map<String, Counter> storage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    @Override
    public int increment(String key, long windowMillis, double refillTokensPerMillis, int cost, RateLimitWindowType rateLimitWindowType) {
        if (rateLimitWindowType.equals(RateLimitWindowType.SLIDING))
            return incrementForSliding(key, windowMillis);
        else if (rateLimitWindowType.equals(RateLimitWindowType.TOKEN_BUCKET))
            return incrementForTokeBucket(key, windowMillis, refillTokensPerMillis, cost);
        return incrementForFixed(key, windowMillis);
    }

    private int incrementForTokeBucket(String key, long windowMillis, double refillTokensPerMillis, int cost) {
        long now = System.currentTimeMillis();

        TokenBucket bucket = tokenBuckets.compute(key, (k, existing) -> {
            // no bucket yet → create full bucket
            if (existing == null) {
                double capacity = refillTokensPerMillis * windowMillis;
                if (capacity < cost) {
                    return new TokenBucket(0, now); // impossible request
                }
                return new TokenBucket(capacity - cost, now);
            }

            // refill tokens based on elapsed time
            long elapsedMillis = now - existing.lastRefillAt;
            double refill = elapsedMillis * refillTokensPerMillis;

            double capacity = refillTokensPerMillis * windowMillis;
            existing.tokens = Math.min(capacity, existing.tokens + refill);
            existing.lastRefillAt = now;

            // not enough tokens → block
            if (existing.tokens < cost) {
                return existing;
            }
            // consume tokens
            existing.tokens -= cost;
            return existing;
        });
        // if tokens went negative or insufficient → blocked
        return bucket.tokens >= 0 ? (int) Math.floor(bucket.tokens) : -1;
    }

    private int incrementForSliding(String key, long windowMillis) {
        long now = System.currentTimeMillis();
        Counter counter = storage.compute(key, (k, existing) -> {
            // no window or expired window → start fresh
            if (existing == null || existing.expireAt <= now) {
                return new Counter(1, now + windowMillis);
            }
            // active window → increment AND slide expiry
            existing.count++;
            existing.expireAt = now + windowMillis;
            return existing;
        });

        return counter.count;
    }

    private int incrementForFixed(String key, long windowMillis) {
        long now = System.currentTimeMillis();

        Counter counter = storage.compute(key, (k, existing) -> {
            // window missing or expired → start new window
            if (existing == null || existing.expireAt <= now) {
                return new Counter(1, now + windowMillis);
            }
            // active window → increment
            existing.count++;
            return existing;
        });
        return counter.count;
    }


}
