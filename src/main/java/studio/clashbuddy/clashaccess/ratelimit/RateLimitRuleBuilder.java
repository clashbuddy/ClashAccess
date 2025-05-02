package studio.clashbuddy.clashaccess.ratelimit;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RateLimitRuleBuilder {

    private final Set<String> paths = new HashSet<>();
    private final Set<RequestMethod> methods = new HashSet<>();
    private int limit = -1;
    private int duration= -1;
    private TimeUnit unit = TimeUnit.NANOSECONDS;
    private String message = "";
    private RateLimitChecker rateLimitChecker;
    private RateLimitKey rateLimitKey;
    public RateLimitRuleBuilder(String... paths) {
        Collections.addAll(this.paths, paths);
    }

    public RateLimitRuleBuilder methods(RequestMethod... methods) {
        Collections.addAll(this.methods, methods);
        return this;
    }

    public RateLimitRuleBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public RateLimitRuleBuilder duration(int duration, TimeUnit unit) {
        this.duration = duration;
        this.unit = unit;
        return this;
    }

    public RateLimitRuleBuilder message(String message) {
        this.message = message;
        return this;
    }

    public RateLimitRuleBuilder checker(RateLimitChecker checker) {
        this.rateLimitChecker =  checker;
        return this;
    }

    public RateLimitRuleBuilder limitKey(RateLimitKey rateLimitKey){
        this.rateLimitKey = rateLimitKey;
        return this;
    }

    public RateLimitRule build() {
        return new RateLimitRule(paths, methods, limit, duration, unit, message, rateLimitChecker,rateLimitKey);
    }
}