package studio.clashbuddy.clashaccess.ratelimit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RateLimitRules {

    private final Set<RateLimitRule> rules = new HashSet<>();
    private RateLimitMetadata rateLimitMetadata;

    private RateLimitRules() {}

    public static RateLimitRules rules(RateLimitRuleBuilder... builders) {
        RateLimitRules config = new RateLimitRules();
        for (RateLimitRuleBuilder builder : builders) {
            config.rules.add(builder.build());
        }
        return config;
    }

    public RateLimitRules defaultLimits(int limit, int duration, TimeUnit unit,String message){
        this.rateLimitMetadata = new RateLimitMetadata(limit,duration,unit,message);
        return this;
    }

    public RateLimitRules defaultLimits(int limit, int duration, TimeUnit unit){
        return defaultLimits(limit,duration,unit,"Too many requests");
    }

    Set<RateLimitRule> getRules() {
        return Collections.unmodifiableSet(rules);
    }

    RateLimitMetadata rateLimitMetadata(){
        return rateLimitMetadata;
    }
}