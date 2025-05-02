package studio.clashbuddy.clashaccess.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class AccessRulesCompiler {
    public static List<CompiledAccessRule> compile(AccessRules accessRules) {
        List<CompiledAccessRule> compiled = new ArrayList<>();

        if (accessRules != null) {
            for (Rule rule : accessRules.getProtectedRules()) {
                compiled.add(new CompiledAccessRule(
                        rule.getPaths().toArray(new String[0]),
                        new HashSet<>(Arrays.asList(rule.getMethods())),
                        rule
                ));
            }
        }

        return compiled;
    }


    public static List<CompiledAccessRule> compilePublic(AccessRules accessRules) {
        List<CompiledAccessRule> compiled = new ArrayList<>();

        if (accessRules != null) {
            for (Rule rule : accessRules.getPublicRules()) {
                compiled.add(new CompiledAccessRule(
                        rule.getPaths().toArray(new String[0]),
                       new HashSet<>(Arrays.asList(rule.getMethods())),
                        rule
                ));
            }
        }

        return compiled;
    }



}
