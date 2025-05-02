package studio.clashbuddy.clashaccess.ratelimit;


import jakarta.servlet.http.HttpServletRequest;

public interface RateLimitKey {
    String limitKey(HttpServletRequest request);
}
