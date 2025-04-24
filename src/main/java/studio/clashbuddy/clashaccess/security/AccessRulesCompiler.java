package studio.clashbuddy.clashaccess.security;

import org.springframework.web.bind.annotation.RequestMethod;
import studio.clashbuddy.clashaccess.security.config.AccessRule;
import studio.clashbuddy.clashaccess.security.config.AccessRules;

import java.util.ArrayList;
import java.util.List;

public class AccessRulesCompiler {
    public static List<CompiledAccessRule> compile(AccessRules accessRules) {
        List<CompiledAccessRule> compiled = new ArrayList<>();

        if (accessRules != null) {
            for (AccessRule rule : accessRules.getRules()) {
                compiled.add(new CompiledAccessRule(
                        rule.getPaths().toArray(new String[0]),
                        rule.getMethods(),
                        rule
                ));
            }
        }

        return compiled;
    }

    public static List<CompiledAccessRule> compilePublic(AccessRules accessRules) {
        List<CompiledAccessRule> compiled = new ArrayList<>();

        if (accessRules != null) {
            for (AccessRule rule : accessRules.getPublicRules()) {
                compiled.add(new CompiledAccessRule(
                        rule.getPaths().toArray(new String[0]),
                        rule.getMethods(),
                        rule
                ));
            }
        }

        return compiled;
    }



}
