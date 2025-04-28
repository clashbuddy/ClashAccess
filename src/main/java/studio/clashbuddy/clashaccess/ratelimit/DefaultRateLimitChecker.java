package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import studio.clashbuddy.clashaccess.utils.IPAddressUtil;

import java.util.concurrent.TimeUnit;


/**
 * Default RateLimitChecker using simple IP-based in-memory counter.
 */
class DefaultRateLimitChecker extends RateLimitChecker {
    private DefaultRateLimitChecker(){}

    private static final DefaultRateLimitChecker INSTANCE = new DefaultRateLimitChecker();

    public static DefaultRateLimitChecker instance() {
        return INSTANCE;
    }

    @Override
    public boolean check(HttpServletRequest request, RateLimitMetadata metadata) {
        String key = buildStorageKey(request);
        long windowMillis = TimeUnit.MILLISECONDS.convert(metadata.getDuration(), metadata.getTimeUnit());

        int currentCount = updateStorageAngFetchLastCount(key, windowMillis);

        return currentCount <= metadata.getLimit();
    }

    private String buildStorageKey(HttpServletRequest request) {
        String clientIp = IPAddressUtil.getClientIpAddress(request);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern == null) {
            pattern = request.getRequestURI(); // fallback if pattern missing
        }
        return clientIp + ":" + pattern;
    }


    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}