package studio.clashbuddy.clashaccess.security;
import org.springframework.web.bind.annotation.RequestMethod;
import studio.clashbuddy.clashaccess.security.config.ProtectedRule;
import studio.clashbuddy.clashaccess.security.config.Rule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CompiledAccessRule {

    private final String[] patterns;
    private final Set<String> methods;
    private final Rule accessRule;

    public CompiledAccessRule(String[] patterns, Set<String> methods, Rule accessRule) {
        this.patterns = patterns;
        this.methods = methods;
        this.accessRule = accessRule;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public Rule getAccessRule() {
        return accessRule;
    }

    public static CompiledAccessRule authorizeAnyRule() {
        Rule rule = new ProtectedRule("/**")
                .methods(RequestMethod.GET);
        return new CompiledAccessRule(rule.getPaths().toArray(String[]::new), new HashSet<>(Arrays.asList(rule.getMethods())), rule);
    }
}