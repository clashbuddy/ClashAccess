package studio.clashbuddy.clashaccess.auth.authentication;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

public abstract class ClashAccessAuthenticationProvider {
    private ClashAccessPasswordEncoder passwordEncoder;
    private String secret;
    private I18nHelper i18nHelper;
    final void setPasswordEncoderAndSecret(ClashAccessPasswordEncoder passwordEncoder,String secret,I18nHelper helper) {
        this.passwordEncoder = passwordEncoder;
        this.i18nHelper = helper;
        this.secret = secret;
    }

    protected final String getSecret() {
        return secret;
    }

    protected final I18nHelper helper(){
        return i18nHelper;
    }


    protected final ClashAccessPasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    public abstract void authenticate(String encryptedPassword,String rawPassword);
    public abstract ClashToken issueToken(ClashAuthPayload payload, double accessExpireInMinutes, double refreshExpireInMinutes);

}
