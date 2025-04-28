package studio.clashbuddy.clashaccess.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.security.config.AccessRules;
import studio.clashbuddy.clashaccess.security.config.PublicRule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


@Service
public class AccessMetadataService {

    private List<CompiledAccessRule> compiledRules;
    private List<CompiledAccessRule> compiledPublicRules;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Autowired(required = false)
    private AccessRules accessRules;
    @Autowired
    private ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties;




    public void setCompiledRules(List<CompiledAccessRule> compiledRules) {
        if (compiledRules != null) {
            // Sort compiled rules by pattern specificity once at startup
            compiledRules.sort(Comparator.comparingInt(this::getBestSpecificityScore).reversed());
        }
        this.compiledRules = compiledRules;
    }

    public void setCompiledPublicRules(List<CompiledAccessRule> compiledPublicRules) {
        var end = clashBuddySecurityClashAccessAppProperties.getEndpointMetadata();
        var metadataRule = new CompiledAccessRule(new String[]{end}, Set.of("GET"), new PublicRule(end).methods(RequestMethod.GET));
        if (compiledPublicRules != null) {
            compiledPublicRules.add(metadataRule);
            compiledPublicRules.sort(Comparator.comparingInt(this::getBestSpecificityScore).reversed());
        } else {
            compiledPublicRules = new ArrayList<>();
            compiledPublicRules.add(metadataRule);
        }
        this.compiledPublicRules = compiledPublicRules;
    }

    public CompiledAccessRule findMatchingRule(String requestPath, String requestMethod) {
        RequestMethod method;
        try {
            method = RequestMethod.valueOf(requestMethod.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null; // Unsupported HTTP method, treat as public
        }

        // 1. Check public rules first
        for (CompiledAccessRule compiled : compiledPublicRules) {
            for (String pattern : compiled.getPatterns()) {
                if (pathMatcher.match(pattern, requestPath)) {
                    if (compiled.getMethods().isEmpty() || compiled.getMethods().contains(method.name())) {
                        return null;
                    }
                }
            }
        }

        for (CompiledAccessRule compiled : compiledRules) {
            for (String pattern : compiled.getPatterns()) {
                if (pathMatcher.match(pattern, requestPath)) {
                    if (compiled.getMethods().isEmpty() || compiled.getMethods().contains(method.name())) {
                        return compiled;
                    }
                }
            }
        }

        if (accessRules.isAuthorizeAnyRequest()) {
            return CompiledAccessRule.authorizeAnyRule();
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
