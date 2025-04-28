package studio.clashbuddy.clashaccess.security.config;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Rule {

    protected final Set<String> paths;
    protected final Set<RequestMethod> methods = new HashSet<>();

    protected Rule(String ... paths) {
        this.paths = new HashSet<>(Arrays.asList(paths));
    }

    public Rule methods(RequestMethod... methods) {
        this.methods.addAll(Arrays.asList(methods));
        return this;
    }

    public Set<String> getPaths() {
        return paths;
    }

    public String[] getMethods() {
        return methods.stream().map(RequestMethod::name).toArray(String[]::new);
    }

    public static ProtectedRule protect(String ...paths) {
        return new ProtectedRule(paths);
    }

    public static PublicRule unprotect(String ... paths) {
        return new PublicRule(paths);
    }
}