package studio.clashbuddy.clashaccess.ratelimit;

public interface RateLimitStorage {

    /**
     * Increments the request count and returns the current count.
     * @param key The storage key
     * @param windowMillis The time window in milliseconds
     * @return The updated request count
     */
    int increment(String key, long windowMillis, double refillTokensPerMillis, int cost, RateLimitWindowType rateLimitWindowType);

}