package studio.clashbuddy.clashaccess.ratelimit;

import java.util.concurrent.TimeUnit;

public class RateLimitMetadata {
    private final int limit;
    private final int duration;
    private final TimeUnit timeUnit;
    private final String message;
    private final RateLimitWindowType windowType;

    public RateLimitMetadata(int limit, int duration, TimeUnit timeUnit, String message, RateLimitWindowType windowType) {
        this.limit = limit;
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.message = message;
        this.windowType = windowType;
    }

    public RateLimitWindowType getWindowType() {
        return windowType;
    }

    public int getLimit() {
        return limit;
    }

    public int getDuration() {
        return duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getMessage() {
        return message;
    }
}