package studio.clashbuddy.clashaccess.security.config;

/**
 * Represents a public (unprotected) access rule.
 */
public class PublicRule extends Rule {

    public PublicRule(String ... paths) {
        super(paths);
    }

    // Nothing special — inherits everything from Rule
}