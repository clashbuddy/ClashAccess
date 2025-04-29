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

    void setRateLimitStorage(RateLimitStorage rateLimitStorage){
        this.rateLimitStorage = rateLimitStorage;
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


    protected String iPAddressKey(HttpServletRequest request) {
        String clientIp = IPAddressUtil.getClientIpAddress(request);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern == null) {
            pattern = request.getRequestURI(); // fallback if pattern missing
        }
        return clientIp + ":"+request.getMethod()+":" + pattern;
    }


}