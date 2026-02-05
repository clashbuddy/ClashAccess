package studio.clashbuddy.clashaccess.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.exceptions.RateLimitException;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static studio.clashbuddy.clashaccess.ratelimit.RateLimitHelper.*;

@Aspect
@Component
class RateLimitAnnotationHandler {
    private static final Logger log = LoggerFactory.getLogger(RateLimitAnnotationHandler.class);
    private final HttpServletRequest request;

    @Autowired
    private ApplicationContext applicationContext;
    private final ObjectProvider<RateLimitStorage> rateLimitStorageProvider;
    private final ObjectProvider<RateLimitChecker> rateLimitCheckerProvider;
    private final ObjectProvider<RateLimitKey> rateLimitKeyProvider;
    private final ObjectProvider<RateLimitRules> rateLimitRulesProvider;
    private final I18nHelper i18nHelper;

    public RateLimitAnnotationHandler(
            HttpServletRequest request,
            ObjectProvider<RateLimitStorage> rateLimitStorageProvider,
            ObjectProvider<RateLimitChecker> rateLimitCheckerProvider,
            ObjectProvider<RateLimitKey> rateLimitKeyProvider,
            ObjectProvider<RateLimitRules> rateLimitRulesProvider,
            I18nHelper i18nHelper
    ) {
        this.request = request;
        this.rateLimitStorageProvider = rateLimitStorageProvider;
        this.rateLimitCheckerProvider = rateLimitCheckerProvider;
        this.rateLimitKeyProvider = rateLimitKeyProvider;
        this.rateLimitRulesProvider = rateLimitRulesProvider;
        this.i18nHelper = i18nHelper;
    }


    @Before("@annotation(studio.clashbuddy.clashaccess.ratelimit.RateLimit)")
    public void before(JoinPoint joinPoint) {

        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        int limit = rateLimit.limit();
        int duration = rateLimit.duration();
        TimeUnit timeUnit = rateLimit.timeUnit();
        RateLimitWindowType rateLimitWindowType = rateLimit.type();
        String message = rateLimit.message();
        Class<? extends RateLimitChecker> checkerClass = rateLimit.checker();
        Class<? extends RateLimitKey> keyLimitClass = rateLimit.limitKey();
        RateLimitMetadata metadata = buildMetadata(limit, duration, timeUnit, message, rateLimitWindowType, rateLimitRulesProvider.getIfAvailable());
        RateLimitChecker checkerInstance;
        RateLimitKey resolveRateLimitKey;
        if (keyLimitClass.equals(RateLimitKey.class))
            resolveRateLimitKey = getDefaultRateLimitKey(rateLimitKeyProvider.getIfAvailable());
        else {
            try {
                resolveRateLimitKey = keyLimitClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                resolveRateLimitKey = getDefaultRateLimitKey(rateLimitKeyProvider.getIfAvailable());
            }
        }

        if (checkerClass.equals(RateLimitChecker.class))
            checkerInstance = getDefaultRateLimitChecker(rateLimitCheckerProvider.getIfAvailable());
        else {
            try {
                checkerInstance = checkerClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                checkerInstance = getDefaultRateLimitChecker(rateLimitCheckerProvider.getIfAvailable());
            }
        }
        checkerInstance.setRateLimitStorage(getDefaultRateLimitStorage(rateLimitStorageProvider.getIfAvailable()), resolveRateLimitKey);
        boolean allowed = checkerInstance.check(request, metadata);
        if (!allowed) {
            String resolved = i18nHelper.i18n(metadata.getMessage());
            throw new RateLimitException(resolved);
        }
    }


}
