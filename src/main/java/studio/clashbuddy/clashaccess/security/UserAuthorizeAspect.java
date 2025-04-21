package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class UserAuthorizeAspect {
    private final HttpServletRequest request;

    public UserAuthorizeAspect(HttpServletRequest request) {
        this.request = request;
    }


    @Before("@annotation(com.spondias.fintech.common.security.UserAuthorize)")
    public void before(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Object[] args = joinPoint.getArgs();
        UserRole[] expectedRoles = method.getAnnotation(UserAuthorize.class).roles();
        for (UserRole expectedRole : expectedRoles) {
            if (expectedRole.equals(UserRole.ALL_ROLES)) {
                if (expectedRoles.length > 1)
                    throw new IllegalArgumentException("Expected roles must be only one role when you add ALL_ROLES");
                break;
            }
        }
        UserPermission[] expectedPermission = method.getAnnotation(UserAuthorize.class).permissions();
        Principal principal = null;
        for (Object arg : args)
            if (arg instanceof Principal principal1) {
                principal = principal1;
                break;
            }

        var p = AccessValidator.validateOneRoleAndPermissions(request, Arrays.asList(expectedRoles), expectedPermission);
        if (principal == null) return;
        principal.setUserId(p.getUserId());
        principal.setRole(p.getRole());
        principal.setPermissions(p.getPermissions());
        principal.setDeviceId(p.getDeviceId());
        principal.setCredentialId(p.getCredentialId());
        principal.setClientAppType(p.getClientAppType());
    }


}
