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
        int currentCount = currentCount(key);
        if (currentCount < metadata.getLimit()) {
            updateCount(key, windowMillis);
            return true;
        }
        return false;
    }


}