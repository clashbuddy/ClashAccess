package studio.clashbuddy.clashaccess.security.config;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder for creating multiple AccessRules cleanly.
 */
public class PublicRuleBuilder {

    private final AccessRules parent;
    private final Set<String> paths = new HashSet<>();
    private final Set<RequestMethod> methods = new HashSet<>();

    public PublicRuleBuilder(AccessRules parent) {
        this.parent = parent;
    }

    public PublicRuleBuilder paths(String... paths) {
        this.paths.addAll(Arrays.asList(paths));
        return this;
    }

    public PublicRuleBuilder methods(RequestMethod... methods) {
        this.methods.addAll(Arrays.asList(methods));
        return this;
    }


    public PublicRuleBuilder and() {
        AccessRule rule = new AccessRule(paths.toArray(String[]::new))
                .methods(methods.toArray(new RequestMethod[0]));
        parent.addPublicRule(rule);
        cleanUp();
        return this;
    }

    private void cleanUp() {
        paths.clear();
        methods.clear();
    }


    public AccessRules build() {
        and();
        return parent;
    }


}
