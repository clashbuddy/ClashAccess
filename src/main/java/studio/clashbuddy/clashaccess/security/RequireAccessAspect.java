package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

import java.lang.reflect.Method;

@Aspect
@Component
class RequireAccessAspect {
    private final HttpServletRequest request;
    @Autowired
    private I18nHelper helper;
    public RequireAccessAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(studio.clashbuddy.clashaccess.security.RequireAccess)")
    public Object before(ProceedingJoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Object[] args = joinPoint.getArgs();
        Class<?>[] parameterTypes =method.getParameterTypes();
        RequireAccess access = method.getAnnotation(RequireAccess.class);
        if (access == null) return joinPoint;
        String[] expectedRoles = access.roles();
        String[] expectedPermission = access.permissions();
        String[] unExpectedRoles = access.excludedRoles();
        String[] unExpectedPermission = access.excludedPermissions();


        var p = AccessValidator.validateOneRoleAndPermissions(request, expectedRoles, unExpectedRoles, expectedPermission, unExpectedPermission,helper);

        for(int i=0; i < parameterTypes.length; i++) {
            if(parameterTypes[i] == AuthorizedUser.class) {
                args[i] = p;
                break;
            }
        }

        try {
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
