package studio.clashbuddy.clashaccess.auth.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

@Service
public class ClashAccessAuthenticationService {


    private final ClashAccessAuthenticationProvider clashAccessAuthenticationProvider;

    public ClashAccessAuthenticationService(@Autowired(required = false) ClashAccessAuthenticationProvider clashAccessAuthenticationProvider, ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ClashAccessPasswordEncoder passwordEncoder, I18nHelper i18nHelper) {
        if (clashAccessAuthenticationProvider == null)
            clashAccessAuthenticationProvider = ClashAccessDefaultAuthenticationProvider.getInstance();
        this.clashAccessAuthenticationProvider = clashAccessAuthenticationProvider;
        this.clashAccessAuthenticationProvider.setPasswordEncoderAndSecret(passwordEncoder, clashBuddySecurityClashAccessAppProperties.getAuthServiceSecret(), i18nHelper);

    }


    public void authenticate(String rawPassword, String encodedPassword) {
        clashAccessAuthenticationProvider.authenticate(encodedPassword, rawPassword);
    }

    public ClashToken issueToken(ClashAuthPayload payload, double accessExpireInMinutes, double refreshExpireInMinutes) {
        return clashAccessAuthenticationProvider.issueToken(payload, accessExpireInMinutes, refreshExpireInMinutes);
    }



}
