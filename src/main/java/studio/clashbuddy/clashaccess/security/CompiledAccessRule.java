package studio.clashbuddy.clashaccess.security;
import studio.clashbuddy.clashaccess.security.config.AccessRule;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

public class CompiledAccessRule {

    private final String[] patterns;
    private final Set<RequestMethod> methods;
    private final AccessRule accessRule;

    public CompiledAccessRule(String[] patterns, Set<RequestMethod> methods, AccessRule accessRule) {
        this.patterns = patterns;
        this.methods = methods;
        this.accessRule = accessRule;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public Set<RequestMethod> getMethods() {
        return methods;
    }

    public AccessRule getAccessRule() {
        return accessRule;
    }

    public static CompiledAccessRule authorizeAnyRule() {
        AccessRule rule = new AccessRule("/**")
                .methods(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.OPTIONS);
        return new CompiledAccessRule(rule.getPaths().toArray(String[]::new), rule.getMethods(), rule);
    }
}