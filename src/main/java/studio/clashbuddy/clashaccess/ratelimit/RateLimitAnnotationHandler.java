package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.exceptions.RateLimitException;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
class RateLimitAnnotationHandler {
    private final HttpServletRequest request;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    private GlobalRateLimitChecker globalRateLimitChecker;
    @Autowired(required = false)
    private GlobalRateLimitStorage globalRateLimitStorage;

    public RateLimitAnnotationHandler(HttpServletRequest request) {
        this.request = request;
    }

    @Before("@annotation(studio.clashbuddy.clashaccess.ratelimit.RateLimit)")
    public void before(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        int limit = rateLimit.limit();
        int duration = rateLimit.duration();
        TimeUnit timeUnit =  rateLimit.timeUnit();
        String message = rateLimit.message();
        RateLimitMetadata metadata = new RateLimitMetadata(limit, duration, timeUnit, message);
        Class<? extends RateLimitChecker> checkerClass = rateLimit.checker();
        RateLimitChecker checkerInstance;
        if(checkerClass.equals(RateLimitChecker.class))
            checkerInstance = getDefaultRateLimitChecker();
        else {
            try {
                checkerInstance = applicationContext.getBean(checkerClass);
            } catch (NoSuchBeanDefinitionException ex) {
                throw new NoSuchBeanDefinitionException(
                        checkerClass.getName(),
                        "❌ ClashAccess RateLimit: RateLimitChecker [" + checkerClass.getSimpleName() + "] is not available in Spring Context!"
                );
            }
        }
        checkerInstance.setRateLimitStorage(getDefaultRateLimitStorage());
        boolean allowed  = checkerInstance.check(request,metadata);
        if(!allowed)
            throw new RateLimitException(message);
    }


    private RateLimitStorage getDefaultRateLimitStorage() {
        if(globalRateLimitStorage == null || globalRateLimitStorage.getRateLimitStorage() == null)
            return  InMemoryRateLimitStorage.instance();
        return globalRateLimitStorage.getRateLimitStorage();
    }

    private RateLimitChecker getDefaultRateLimitChecker() {
        if(globalRateLimitChecker == null || globalRateLimitChecker.getRateLimitChecker() == null)
           return DefaultRateLimitChecker.instance();
        return globalRateLimitChecker.getRateLimitChecker();
    }

}
