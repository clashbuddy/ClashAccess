package studio.clashbuddy.clashaccess.security;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Rule {

    protected final Set<String> paths;
    protected final Set<RequestMethod> methods = new HashSet<>();

    protected Rule(String ... paths) {
        this.paths = new HashSet<>(Arrays.asList(paths));
    }

    public Rule methods(RequestMethod methods) {
        this.methods.add(methods);
        return this;
    }

    void addListMethods(RequestMethod ... methods){
        this.methods.addAll(Arrays.asList(methods));
    }

    Set<String> getPaths() {
        return paths;
    }

     String[] getMethods() {
        return methods.stream().map(RequestMethod::name).toArray(String[]::new);
    }

    public static ProtectedRule protect(String path) {
        if (path == null || path.isEmpty())
            throw new IllegalStateException("Empty protected path, provide path");
        return new ProtectedRule(path);
    }

    public static PublicRule unprotect(String path) {
        if (path == null || path.isEmpty())
            throw new IllegalStateException("Empty unprotected path, provide path");
        return new PublicRule(path);
    }
}