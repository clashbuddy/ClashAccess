package studio.clashbuddy.clashaccess.security.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class AccessRules {

    private final Set<ProtectedRule> protectedRules = new HashSet<>();
    private final Set<PublicRule> publicRules = new HashSet<>();
    private boolean authorizeAnyRequest = false;

    private AccessRules() {
    }

    public static AccessRules rules(Rule... rules) {
        AccessRules accessRules = new AccessRules();
        for (Rule rule : rules) {
            if (rule instanceof ProtectedRule) {
                accessRules.protectedRules.add((ProtectedRule) rule);
            } else if (rule instanceof PublicRule) {
                accessRules.publicRules.add((PublicRule) rule);
            } else {
                throw new IllegalArgumentException("Unknown rule type: " + rule.getClass());
            }
        }
        return accessRules;
    }

    public AccessRules authorizeAny() {
        this.authorizeAnyRequest = true;
        return this;
    }

    public Set<ProtectedRule> getProtectedRules() {
        return Collections.unmodifiableSet(protectedRules);
    }

    public Set<PublicRule> getPublicRules() {
        return Collections.unmodifiableSet(publicRules);
    }

    public boolean isAuthorizeAnyRequest() {
        return authorizeAnyRequest;
    }
}