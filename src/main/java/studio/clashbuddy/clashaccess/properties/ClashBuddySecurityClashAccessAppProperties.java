package studio.clashbuddy.clashaccess.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.auth-service")
public class ClashBuddySecurityClashAccessAppProperties {

    private String secret;

    public String getAuthServiceSecret() {
        return secret;
    }

    public void setSecret(String authServiceSecret) {
        this.secret = authServiceSecret;
    }
}
