package studio.clashbuddy.clashaccess.auth.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;

@Service
public class ClashAccessAuthenticationService {

    @Autowired(required = false)
    private ClashAccessAuthenticationProvider clashAccessAuthenticationProvider;
    @Autowired
    private ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties;

    @Autowired
    private ClashAccessPasswordEncoder passwordEncoder;

    public void authenticate(String rawPassword, String encodedPassword) {
        setRequiredProperties();
        clashAccessAuthenticationProvider.authenticate(encodedPassword, rawPassword);
    }

    public ClashToken issueToken(ClashAuthPayload payload, double accessExpireInMinutes, double refreshExpireInMinutes) {
        return clashAccessAuthenticationProvider.issueToken(payload, accessExpireInMinutes, refreshExpireInMinutes);
    }

    private void setRequiredProperties() {
        if (clashAccessAuthenticationProvider == null)
            clashAccessAuthenticationProvider = getDefaultAuthenticationProvider();
        clashAccessAuthenticationProvider.setPasswordEncoderAndSecret(passwordEncoder, clashBuddySecurityClashAccessAppProperties.getAuthServiceSecret());
    }


    private ClashAccessAuthenticationProvider getDefaultAuthenticationProvider() {
        return ClashAccessDefaultAuthenticationProvider.getInstance();
    }

}
