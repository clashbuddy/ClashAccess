package studio.clashbuddy.clashaccess.ratelimit;


public final class GlobalRateLimitChecker {
    private final RateLimitChecker rateLimitChecker;

    public GlobalRateLimitChecker(RateLimitChecker rateLimitChecker) {
        this.rateLimitChecker = rateLimitChecker;
    }

    RateLimitChecker getRateLimitChecker() {
        return rateLimitChecker;
    }
}
