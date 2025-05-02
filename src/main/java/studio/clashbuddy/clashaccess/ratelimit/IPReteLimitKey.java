package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import studio.clashbuddy.clashaccess.utils.IPAddressUtil;

class IPReteLimitKey implements RateLimitKey{
    private static  IPReteLimitKey INSTANCE;
    static IPReteLimitKey instance(){
        if(INSTANCE ==null)
            INSTANCE = new IPReteLimitKey();
        return INSTANCE;
    }
    @Override
    public String limitKey(HttpServletRequest request) {
        String clientIp = IPAddressUtil.getClientIpAddress(request);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern == null) {
            pattern = request.getRequestURI(); // fallback if pattern missing
        }
        return clientIp + ":"+request.getMethod()+":" + pattern;
    }

}
