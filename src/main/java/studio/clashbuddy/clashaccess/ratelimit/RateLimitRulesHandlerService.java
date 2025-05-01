package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import studio.clashbuddy.clashaccess.exceptions.RateLimitException;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

import static studio.clashbuddy.clashaccess.ratelimit.RateLimitHelper.*;
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)

@Service
public class RateLimitRulesHandlerService {
    @Autowired
    private I18nHelper i18nHelper;
    @Autowired(required = false)
    private RateLimitRules rateLimitRules;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Autowired(required = false)
    private GlobalRateLimitChecker globalRateLimitChecker;
    @Autowired(required = false)
    private GlobalRateLimitStorage globalRateLimitStorage;


    public void handleRateLimit(HttpServletRequest request, Object handler){
        if (!(handler instanceof HandlerMethod handlerMethod)) return;
        if(rateLimitRules == null) return;
        String path = request.getRequestURI();
        String method = request.getMethod();
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        RateLimitRule rule = getRule(rateLimit, path,method);
        if(rule == null) return;
        validateLimit(rule,request);
    }

    private RateLimitRule getRule(RateLimit rateLimit,String path, String method){
        if(rateLimit !=null)return null;

        for (RateLimitRule rule : rateLimitRules.getRules()){
            boolean matchesPath = rule.getPaths().stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, path));
            boolean matchesMethod = rule.getMethods().isEmpty() ||
                    rule.getMethods().stream().anyMatch(m -> m.name().equalsIgnoreCase(method));
            if (matchesPath && matchesMethod) {
                return rule;
            }
        }
        return null;
    }

    private void validateLimit(RateLimitRule rule,HttpServletRequest request){
        RateLimitChecker checkerInstance;
        if(rule.getChecker() == null)
            checkerInstance = getDefaultRateLimitChecker(globalRateLimitChecker);
        else
            checkerInstance = rule.getChecker();
        checkerInstance.setRateLimitStorage(getDefaultRateLimitStorage(globalRateLimitStorage));

        RateLimitMetadata metadata = buildMetadata(rule.getLimit(), rule.getDuration(), rule.getUnit(), rule.getMessage(), rateLimitRules);
        boolean allowed  = checkerInstance.check(request,metadata);
        if(!allowed)
            throw new RateLimitException(i18nHelper.i18n(metadata.getMessage()));
    }



}
