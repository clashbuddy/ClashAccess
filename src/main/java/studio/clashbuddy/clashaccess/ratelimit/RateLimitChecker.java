package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import studio.clashbuddy.clashaccess.utils.IPAddressUtil;

/**
 * Core abstract RateLimit checker class.
 *
 * Developers can extend this class to implement their own logic for rate limiting decisions.
 */
public abstract class RateLimitChecker {

    private RateLimitStorage rateLimitStorage;
    protected RateLimitKey rateLimitKey;
    void setRateLimitStorage(RateLimitStorage rateLimitStorage,RateLimitKey rateLimitKey){
        this.rateLimitStorage = rateLimitStorage;
        this.rateLimitKey = rateLimitKey;
    }



    /**
     * Decides whether the request should be allowed based on rate limit.
     *
     * @param request The current HTTP request
     * @return true to aRateLimitCheckerllow the request, false to reject with 429
     */
    public abstract boolean check(HttpServletRequest request, RateLimitMetadata rateLimitMetadata);

    protected int updateCount(String key, long windowMillis){
        return rateLimitStorage.increment(key,windowMillis);
    }

    protected int currentCount(String key){
        return rateLimitStorage.currentCount(key);
    }




}