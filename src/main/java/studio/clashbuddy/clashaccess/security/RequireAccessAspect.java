package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
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

    @Before("@annotation(studio.clashbuddy.clashaccess.security.RequireAccess)")
    public void before(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Object[] args = joinPoint.getArgs();
        RequireAccess access = method.getAnnotation(RequireAccess.class);
        if (access == null) return;
        String[] expectedRoles = access.roles();
        String[] expectedPermission = access.permissions();
        String[] unExpectedRoles = access.excludedRoles();
        String[] unExpectedPermission = access.excludedPermissions();
        AuthorizedUser authorizedUser = null;
        for (Object arg : args)
            if (arg instanceof AuthorizedUser authorizedUser1) {
                authorizedUser = authorizedUser1;
                break;
            }

        var p = AccessValidator.validateOneRoleAndPermissions(request, expectedRoles, unExpectedRoles, expectedPermission, unExpectedPermission,helper);
        if (authorizedUser == null) return;
        authorizedUser.setUserId(p.getUserId());
        authorizedUser.setPermissions(p.getPermissions());
        authorizedUser.setRoles(p.getRoles());
    }

}
