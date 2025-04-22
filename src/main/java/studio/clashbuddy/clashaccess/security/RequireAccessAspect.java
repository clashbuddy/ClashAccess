package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RequireAccessAspect {
    private final HttpServletRequest request;
    public RequireAccessAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before("@annotation(studio.clashbuddy.clashaccess.security.RequireAccess)")
    public void before(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Object[] args = joinPoint.getArgs();
        RequireAccess access = method.getAnnotation(RequireAccess.class);
        if(access == null) return;
        String[] expectedRoles =access.roles();
        String[] expectedPermission = access.permissions();
        String[] unExpectedRoles = access.excludedRoles();
        String[] unExpectedPermission = access.excludedPermissions();
        String[] extraAtt = access.extraSecurityAttributes();
        AuthorizedUser authorizedUser = null;
        for (Object arg : args)
            if (arg instanceof AuthorizedUser authorizedUser1) {
                authorizedUser = authorizedUser1;
                break;
            }

        var p = AccessValidator.validateOneRoleAndPermissions(request,expectedRoles,unExpectedRoles,expectedPermission,unExpectedPermission,extraAtt);
        if (authorizedUser == null) return;
        authorizedUser.setUserId(p.getUserId());
        authorizedUser.setPermissions(p.getPermissions());
        authorizedUser.setRoles(p.getRoles());
    }

}
