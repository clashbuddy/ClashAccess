package studio.clashbuddy.clashaccess.auth.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

@Service
public class ClashAccessAuthenticationService {

    @Autowired(required = false)
    private ClashAccessAuthenticationProvider clashAccessAuthenticationProvider;
    @Autowired
    private ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties;

    @Autowired
    private ClashAccessPasswordEncoder passwordEncoder;

    @Autowired
    private I18nHelper i18nHelper;

    public void authenticate(String rawPassword, String encodedPassword) {
        setRequiredProperties();
        clashAccessAuthenticationProvider.authenticate(encodedPassword, rawPassword);
    }

    public ClashToken issueToken(ClashAuthPayload payload, double accessExpireInMinutes, double refreshExpireInMinutes,String tokenVersion) {
        return clashAccessAuthenticationProvider.issueToken(payload, accessExpireInMinutes, refreshExpireInMinutes,tokenVersion);
    }

    private void setRequiredProperties() {
        if (clashAccessAuthenticationProvider == null)
            clashAccessAuthenticationProvider = getDefaultAuthenticationProvider();
        clashAccessAuthenticationProvider.setPasswordEncoderAndSecret(passwordEncoder, clashBuddySecurityClashAccessAppProperties.getAuthServiceSecret(),i18nHelper);
    }


    private ClashAccessAuthenticationProvider getDefaultAuthenticationProvider() {
        return ClashAccessDefaultAuthenticationProvider.getInstance();
    }

}
