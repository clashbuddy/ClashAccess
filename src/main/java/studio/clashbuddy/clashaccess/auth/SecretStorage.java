package studio.clashbuddy.clashaccess.auth;

import org.springframework.stereotype.Component;

@Component
public class SecretStorage {
    private String currentSecret;

    public void setSecret(String secret) {
        this.currentSecret = secret;
    }

    public String getSecret() {
        return currentSecret;
    }
}
