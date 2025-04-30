package studio.clashbuddy.clashaccess.auth.authentication;

import studio.clashbuddy.clashaccess.auth.SecretStorage;

public abstract class ClashAccessAuthenticationProvider {
    private ClashAccessPasswordEncoder passwordEncoder;
    private String secret;
    final void setPasswordEncoderAndSecret(ClashAccessPasswordEncoder passwordEncoder,String secret) {
        this.passwordEncoder = passwordEncoder;
        this.secret = secret;
    }

    protected final String getSecret() {
        return secret;
    }


    protected final ClashAccessPasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    public abstract void authenticate(String encryptedPassword,String rawPassword);
    public abstract ClashToken issueToken(ClashAuthPayload payload, double accessExpireInMinutes, double refreshExpireInMinutes);

}
