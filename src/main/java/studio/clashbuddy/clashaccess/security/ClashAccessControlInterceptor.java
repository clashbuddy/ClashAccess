package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import studio.clashbuddy.clashaccess.ratelimit.RateLimitRulesHandlerService;
import studio.clashbuddy.clashaccess.security.config.AccessRules;
import studio.clashbuddy.clashaccess.security.config.ProtectedRule;
import studio.clashbuddy.clashaccess.utils.I18nHelper;
@Component
class ClashAccessControlInterceptor implements HandlerInterceptor {

    @Autowired(required = false)
    private AccessRules accessRules;


    @Autowired
    private AccessMetadataService accessMetadataService;

    @Autowired
    private RateLimitRulesHandlerService rateLimitRulesHandlerService;

    @Autowired
    private I18nHelper helper;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (HttpStatus.valueOf(response.getStatus()).is4xxClientError()) {
            return true;
        }
        rateLimitRulesHandlerService.handleRateLimit(request,handler);

        if(accessRules == null) return true;

        String path = request.getRequestURI();
        String method = request.getMethod();
        CompiledAccessRule compiledRule = accessMetadataService.findMatchingRule(path, method);
        if (compiledRule == null) {
            return true;
        }

        ProtectedRule rule = (ProtectedRule) compiledRule.getAccessRule();

        var authorizedUser = AccessValidator.validateOneRoleAndPermissions(
                request,
                rule.getRoles().toArray(new String[0]),
                rule.getExcludedRoles().toArray(new String[0]),
                rule.getPermissions().toArray(new String[0]),
                rule.getExcludedPermissions().toArray(new String[0]),helper
        );
        request.setAttribute("authorizedUser", authorizedUser);
        return true;
    }


}