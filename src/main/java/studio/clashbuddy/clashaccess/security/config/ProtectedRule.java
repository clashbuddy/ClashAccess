package studio.clashbuddy.clashaccess.security.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ProtectedRule extends Rule {

    private final Set<String> roles = new HashSet<>();
    private final Set<String> permissions = new HashSet<>();

    private final Set<String> excludedRoles = new HashSet<>();
    private final Set<String> excludedPermissions = new HashSet<>();

    public ProtectedRule(String...paths) {
        super(paths);
    }

    public ProtectedRule roles(String... roles) {
        this.roles.addAll(Arrays.asList(roles));
        return this;
    }

    public ProtectedRule permissions(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public ProtectedRule excludedPermission(String... excludedPermissions) {
        this.excludedPermissions.addAll(Arrays.asList(excludedPermissions));
        return this;
    }

    public ProtectedRule excludedRoles(String... excludedRoles) {
        this.excludedRoles.addAll(Arrays.asList(excludedRoles));
        return this;
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public Set<String> getExcludedRoles() {
        return excludedRoles;
    }

    public Set<String> getExcludedPermissions() {
        return excludedPermissions;
    }
}