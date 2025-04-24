package studio.clashbuddy.clashaccess.security.config;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder for creating multiple AccessRules cleanly.
 */
public class AccessRuleBuilder {

    private final AccessRules parent;
    private final Set<String> paths = new HashSet<>();
    private final Set<RequestMethod> methods = new HashSet<>();
    private final Set<String> roles = new HashSet<>();
    private final Set<String> permissions = new HashSet<>();
    private final Set<String> excludedRoles = new HashSet<>();
    private final Set<String> excludedPermissions = new HashSet<>();

    public AccessRuleBuilder(AccessRules parent) {
        this.parent = parent;
    }

    public AccessRuleBuilder paths(String... paths) {
        this.paths.addAll(Arrays.asList(paths));
        return this;
    }

    public AccessRuleBuilder methods(RequestMethod... methods) {
        this.methods.addAll(Arrays.asList(methods));
        return this;
    }

    public AccessRuleBuilder roles(String... roles) {
        this.roles.addAll(Arrays.asList(roles));
        return this;
    }

    public AccessRuleBuilder permissions(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public AccessRuleBuilder excludedRoles(String... roles) {
        this.excludedRoles.addAll(Arrays.asList(roles));
        return this;
    }

    public AccessRuleBuilder excludedPermissions(String... permissions) {
        this.excludedPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * Finalize building and return to parent AccessRules.
     */
    public AccessRuleBuilder and() {
        AccessRule rule = new AccessRule(paths.toArray(String[]::new))
                .methods(methods.toArray(new RequestMethod[0]))
                .roles(roles.toArray(new String[0]))
                .permissions(permissions.toArray(new String[0]))
                .excludedRoles(excludedRoles.toArray(new String[0]))
                .excludedPermissions(excludedPermissions.toArray(new String[0]));
        parent.addRule(rule);
        cleanUp();

        return this;
    }

    private void cleanUp() {
        paths.clear();
        excludedRoles.clear();
        roles.clear();
        permissions.clear();
        excludedPermissions.clear();
        methods.clear();
    }


    public AccessRules build() {
        and();
        return parent;
    }


}
