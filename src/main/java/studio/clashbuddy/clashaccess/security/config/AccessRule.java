package studio.clashbuddy.clashaccess.security.config;

import jakarta.annotation.Nonnull;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a security rule for one or more endpoint patterns.
 */
public class AccessRule {

    private final Set<String> paths;
    private final Set<RequestMethod> methods;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Set<String> exRoles;
    private final Set<String> exPermissions;

    public AccessRule(@Nonnull String... paths) {
        this.paths = new HashSet<>(Arrays.asList(paths));
        this.methods = EnumSet.noneOf(RequestMethod.class); // empty initially
        this.roles = new HashSet<>();
        this.permissions = new HashSet<>();
        this.exRoles = new HashSet<>();
        this.exPermissions = new HashSet<>();
    }

    public AccessRule methods(@Nonnull RequestMethod... methods) {
        this.methods.addAll(Arrays.asList(methods));
        return this;
    }

    public AccessRule roles(@Nonnull String... roles) {
        this.roles.addAll(Arrays.asList(roles));
        return this;
    }

    public AccessRule permissions(@Nonnull String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public AccessRule excludedRoles(@Nonnull String... roles) {
        this.exRoles.addAll(Arrays.asList(roles));
        return this;
    }

    public AccessRule excludedPermissions(@Nonnull String... permissions) {
        this.exPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    @Nonnull
    public Set<String> getPaths() {
        return Set.copyOf(paths);
    }

    @Nonnull
    public Set<RequestMethod> getMethods() {
        return Set.copyOf(methods);
    }

    @Nonnull
    public String[] getMethodsStrings() {
        return methods.stream()
                .map(RequestMethod::toString)
                .toArray(String[]::new);
    }

    @Nonnull
    public Set<String> getRoles() {
        return Set.copyOf(roles);
    }

    @Nonnull
    public Set<String> getPermissions() {
        return Set.copyOf(permissions);
    }

    @Nonnull
    public Set<String> getExcludedRoles() {
        return Set.copyOf(exRoles);
    }

    @Nonnull
    public Set<String> getExcludedPermissions() {
        return Set.copyOf(exPermissions);
    }
}
