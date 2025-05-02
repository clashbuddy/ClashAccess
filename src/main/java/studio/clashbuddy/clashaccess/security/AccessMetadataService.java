package studio.clashbuddy.clashaccess.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;
import java.util.stream.Stream;


@Service
public class AccessMetadataService {


    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Autowired(required = false)
    private AccessRules accessRules;



    public ProtectedRule findMatchingRule(String requestPath, String requestMethod) {
        RequestMethod method;
        try {
            method = RequestMethod.valueOf(requestMethod.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null; // Unsupported HTTP method, treat as public
        }



        // 1. Check public rules first
        for (Rule rule : accessRules.getPublicRules()) {
            for (String pattern : rule.getPaths()) {
                if (pathMatcher.match(pattern, requestPath)) {
                    var methods = Arrays.asList(rule.getMethods());
                    if (methods.isEmpty() || methods.contains(method.name())) {
                        return null;
                    }
                }
            }
        }

        for (ProtectedRule rule : accessRules.getProtectedRules()) {
            for (String pattern : rule.getPaths()) {
                if (pathMatcher.match(pattern, requestPath)) {
                    var methods = Arrays.asList(rule.getMethods());
                    if (methods.isEmpty() || methods.contains(method.name())) {
                        return rule;
                    }
                }
            }
        }

        if (accessRules.isAuthorizeAnyRequest()) {
           var  protectRules =  new ProtectedRule("/**");
           protectRules.addListMethods(RequestMethod.values());
           return protectRules;
        }

        return null; // No matching rule, treat as public
    }

    /**
     * Calculate a specificity score for sorting patterns.
     * Higher = more specific.
     */
    private int getBestSpecificityScore(CompiledAccessRule rule) {
        return Stream.of(rule.getPatterns())

                .mapToInt(this::specificityScore)
                .max()
                .orElse(0);
    }

    private int specificityScore(String pattern) {
        int score = 0;
        for (char c : pattern.toCharArray()) {
            if (c == '/') {
                score += 10; // Each path segment boosts score
            } else if (c == '*') {
                score -= 5; // Penalize wildcards
            } else if (c == '{') {
                score -= 2; // Penalize path variables slightly
            } else {
                score += 1; // Normal characters boost slightly
            }
        }
        return score;
    }
}
