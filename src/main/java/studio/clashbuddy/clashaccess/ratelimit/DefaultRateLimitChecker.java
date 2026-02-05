package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.TimeUnit;


/**
 * Default RateLimitChecker using simple IP-based in-memory counter.
 */
class DefaultRateLimitChecker extends RateLimitChecker {
    private DefaultRateLimitChecker() {
    }

    private static DefaultRateLimitChecker INSTANCE;

    public static DefaultRateLimitChecker instance() {
        if(INSTANCE == null)
            INSTANCE = new DefaultRateLimitChecker();
        return INSTANCE;
    }

    @Override
    public boolean check(HttpServletRequest request, RateLimitMetadata metadata) {
        String key = rateLimitKey.limitKey(request);
        long windowMillis = TimeUnit.MILLISECONDS.convert(metadata.getDuration(), metadata.getTimeUnit());
        int limit = metadata.getLimit();
        if(RateLimitWindowType.TOKEN_BUCKET.equals(metadata.getWindowType()))
            return checkTokenBucket(key, windowMillis, limit);
        if(RateLimitWindowType.SLIDING.equals(metadata.getWindowType()))
            return checkSlideWindow(key, windowMillis, limit);
        return checkFixedWindow(key, windowMillis, limit);
    }

    private boolean checkTokenBucket(String key, long windowMillis, int limit){
        double refillRatePerMillis = (double) limit / windowMillis;
        int cost = 1;
        double remainingTokens = updateCount(key, windowMillis, refillRatePerMillis, cost, RateLimitWindowType.TOKEN_BUCKET);
        return remainingTokens >= 0;
    }

    private boolean checkFixedWindow(String key, long windowMillis, int limit) {
        int currentCount = updateCount(key, windowMillis,RateLimitWindowType.FIXED);
        return currentCount <= limit;
    }

    private boolean checkSlideWindow(String key, long windowMillis, int limit) {
        int currentCount = updateCount(key, windowMillis, RateLimitWindowType.SLIDING);
        return currentCount <= limit;
    }






}