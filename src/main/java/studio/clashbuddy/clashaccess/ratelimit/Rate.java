package studio.clashbuddy.clashaccess.ratelimit;

public class Rate {
    public static RateLimitRuleBuilder paths(String... paths) {
        return new RateLimitRuleBuilder(paths);
    }

    private Rate() {} // prevent instantiation
}