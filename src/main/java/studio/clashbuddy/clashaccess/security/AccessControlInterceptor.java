package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import studio.clashbuddy.clashaccess.security.config.AccessRule;
import studio.clashbuddy.clashaccess.security.config.AccessRules;

@Component
class AccessControlInterceptor  implements HandlerInterceptor {

    @Autowired(required = false)
    private AccessRules accessRules;
    @Autowired
    private AccessMetadataService accessMetadataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(accessRules == null) return true;
        String path = request.getRequestURI();
        String method = request.getMethod();
        CompiledAccessRule compiledRule = accessMetadataService.findMatchingRule(path, method);
        if (compiledRule == null) {
            return true; // Public endpoint
        }

        AccessRule rule = compiledRule.getAccessRule();

        var authorizedUser = AccessValidator.validateOneRoleAndPermissions(
                request,
                rule.getRoles().toArray(new String[0]),
                rule.getExcludedRoles().toArray(new String[0]),
                rule.getPermissions().toArray(new String[0]),
                rule.getExcludedPermissions().toArray(new String[0])
        );
        request.setAttribute("authorizedUser", authorizedUser);
        return true;
    }
}