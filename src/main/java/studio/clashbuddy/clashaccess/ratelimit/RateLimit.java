package studio.clashbuddy.clashaccess.ratelimit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Maximum number of allowed requests in the time window.
     */
    int limit() default -1;

    /**
     * Duration of the time window (e.g., 1 minute, 5 seconds).
     */
    int duration() default -1;

    /**
     * Unit of time for the duration.
     */
    TimeUnit timeUnit() default TimeUnit.NANOSECONDS;

    /**
     * (Optional) Custom key resolver class.
     * If not provided, default resolver (by IP) will be used.
     */
    Class<? extends RateLimitChecker> checker() default RateLimitChecker.class;

    /**
     * (Optional) Custom error message when rate limit is exceeded.
     */
    String message() default "";
}