package studio.clashbuddy.clashaccess.ratelimit;

public class GlobalRateLimitStorage {
    private final RateLimitStorage rateLimitStorage;

    public GlobalRateLimitStorage(RateLimitStorage rateLimitStorage) {
        this.rateLimitStorage = rateLimitStorage;
    }

    RateLimitStorage getRateLimitStorage(){
        return rateLimitStorage;
    }
}
