package studio.clashbuddy.clashaccess.ratelimit;

import java.util.concurrent.TimeUnit;

class RateLimitHelper {


    public static RateLimitStorage getDefaultRateLimitStorage(RateLimitStorage rateLimitStorage) {
        if(rateLimitStorage == null)
            return  InMemoryRateLimitStorage.instance();
        return rateLimitStorage;
    }

    public static RateLimitChecker getDefaultRateLimitChecker(RateLimitChecker rateLimitChecker) {
        if(rateLimitChecker == null)
            return DefaultRateLimitChecker.instance();
        return rateLimitChecker;
    }

    public static RateLimitKey getDefaultRateLimitKey(RateLimitKey rateLimitKey) {
        if(rateLimitKey ==  null)
            return IPReteLimitKey.instance();
        return rateLimitKey;
    }




    public static RateLimitMetadata buildMetadata(int limit, int duration, TimeUnit unit,String message,RateLimitRules rateLimitRules){
        if(rateLimitRules==null)
            return defaultMetadata(limit,duration,unit,message);
        var rate = rateLimitRules.rateLimitMetadata();
        if(rate == null)
            return defaultMetadata(limit,duration,unit,message);
        if(limit <= -1)
            limit = rate.getLimit();
        if(duration <= -1)
            duration = rate.getDuration();
        if(unit.equals(TimeUnit.NANOSECONDS))
            unit = rate.getTimeUnit();
        if(message.isEmpty())
            message = rate.getMessage();
        return  defaultMetadata(limit,duration,unit,message);
    }

    private static RateLimitMetadata defaultMetadata(int limit, int duration, TimeUnit unit,String message){
        if(limit <=-1)
            limit = 100;
        if(duration <=-1)
            duration = 1;
        if (unit.equals(TimeUnit.NANOSECONDS))
            unit = TimeUnit.MINUTES;
        if(message.isEmpty())
            message ="{clashaccess.rate.limit}";
        return new RateLimitMetadata(limit,duration,unit,message);
    }


}
