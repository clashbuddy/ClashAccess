package studio.clashbuddy.clashaccess.security.config;

import jakarta.annotation.Nonnull;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds all security rules for a service.
 */
public class AccessRules {

    private final Set<AccessRule> rules = new HashSet<>();
    private final Set<AccessRule> publicRules = new HashSet<>();
    private boolean authorizeAnyRequest = false;
    public AccessRules() {
    }


    public AccessRules(@Nonnull AccessRule... rules) {
        Collections.addAll(this.rules, rules);
    }

    public AccessRules(@Nonnull Set<AccessRule> rules) {
        this.rules.addAll(rules);
    }


    public AccessRuleBuilder protect() {
        return new AccessRuleBuilder(this);
    }


    public PublicRuleBuilder unprotect() {
        return new PublicRuleBuilder(this);
    }

    public AccessRules authorizeAny() {
        this.authorizeAnyRequest = true;
        return this;
    }

    protected void addRule(AccessRule rule) {
        this.rules.add(rule);
    }
    protected void addPublicRule(AccessRule rule) {
        this.publicRules.add(rule);
    }

    @Nonnull
    public Set<AccessRule> getRules() {
        return Collections.unmodifiableSet(rules);
    }

    public Set<AccessRule> getPublicRules() {
        return publicRules;
    }

    public boolean isAuthorizeAnyRequest() {
        return authorizeAnyRequest;
    }




}
