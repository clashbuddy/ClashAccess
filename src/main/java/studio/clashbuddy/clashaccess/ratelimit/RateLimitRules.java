package studio.clashbuddy.clashaccess.ratelimit;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RateLimitRules {

    private final Set<RateLimitRule> rules = new HashSet<>();
    private RateLimitMetadata rateLimitMetadata;

    private RateLimitRules() {}

    public static RateLimitRules rules(RateLimitRule... builders) {
        RateLimitRules config = new RateLimitRules();
        config.rules.addAll(Arrays.asList(builders));
        return config;
    }

    public RateLimitRules defaultLimits(int limit, int duration, TimeUnit unit,String message,RateLimitWindowType windowType) {
        this.rateLimitMetadata = new RateLimitMetadata(limit,duration,unit,message,windowType);
        return this;
    }

    public RateLimitRules defaultLimits(int limit, int duration, TimeUnit unit,RateLimitWindowType windowType) {
        return defaultLimits(limit,duration,unit,"{clashaccess.rate.limit}",windowType);
    }

    Set<RateLimitRule> getRules() {
        return Collections.unmodifiableSet(rules);
    }

    RateLimitMetadata rateLimitMetadata(){
        return rateLimitMetadata;
    }
}