package studio.clashbuddy.clashaccess.ratelimit;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RateLimitRule {

    private final Set<String> paths;
    private final Set<RequestMethod> methods;
    private final int limit;
    private final int duration;
    private final TimeUnit unit;
    private final String message;
    private final RateLimitChecker checker;
    public RateLimitRule(Set<String> paths, Set<RequestMethod> methods, int limit, int duration, TimeUnit unit, String message, RateLimitChecker checker) {
        this.paths = paths;
        this.methods = methods;
        this.limit = limit;
        this.duration = duration;
        this.unit = unit;
        this.message = message;
        this.checker = checker;
    }

    Set<String> getPaths() {
        return paths;
    }

    Set<RequestMethod> getMethods() {
        return methods;
    }

    int getLimit() {
        return limit;
    }

    int getDuration() {
        return duration;
    }

    TimeUnit getUnit() {
        return unit;
    }

    String getMessage() {
        return message;
    }

    RateLimitChecker getChecker() {
        return checker;
    }
}