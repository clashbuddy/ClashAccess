package studio.clashbuddy.clashaccess.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.application")
public class ClashBuddySecurityClashAccessAppProperties {
    private AccessCredential access = new AccessCredential();
    private boolean enabled = true;
    private boolean scan = true;
    private boolean isAuthService=false;
    private String authServiceEndpoint;
    private String authServiceKey;
    private boolean shareAuthSecretKey = false;
    private String authServiceSecret;
    private List<AccessCredential> serviceEps = new ArrayList<>();
    public ClashBuddySecurityClashAccessAppProperties() {
        setDefaults();
    }

    public boolean isShareAuthSecretKey() {
        return shareAuthSecretKey;
    }

    public void setShareAuthSecretKey(boolean shareAuthSecretKey) {
        this.shareAuthSecretKey = shareAuthSecretKey;
    }

    public String getAuthServiceSecret() {
        return authServiceSecret;
    }

    public void setAuthServiceSecret(String authServiceSecret) {
        this.authServiceSecret = authServiceSecret;
    }

    public List<AccessCredential> getServiceEps() {
        return serviceEps;
    }

    public void setServiceEps(List<AccessCredential> serviceEps) {
        this.serviceEps = serviceEps;
    }


    public String getAuthServiceEndpoint() {
        return authServiceEndpoint;
    }

    public void setAuthServiceEndpoint(String authServiceEndpoint) {
        this.authServiceEndpoint = authServiceEndpoint;
    }

    public String getAuthServiceKey() {
        return authServiceKey;
    }

    public void setAuthServiceKey(String authServiceKey) {
        this.authServiceKey = authServiceKey;
    }

    public void setAccess(AccessCredential access) {
        this.access = access;
    }



    public String getApiKey() {

        return access.getKey();
    }


    public String getEndpointMetadata() {

        return access.getEndpoint();
    }

    public boolean isNotChanged() {
        if(access == null) return true;
        return getEndpointMetadata().equalsIgnoreCase(access.getEndpoint()) && getDefaultApiKey().equalsIgnoreCase(access.getKey());
    }

    public String getDefaultEndpoint() {
        return "/clashbuddy-clash-access/endpoint-metadata";
    }

    public String getDefaultApiKey() {
        return "access";
    }

    public void setDefaults() {
        access.setEndpoint(getDefaultEndpoint());
        access.setKey(getDefaultApiKey());

    }


    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isScan() {
        return scan;
    }

    public void setScan(boolean scan) {
        this.scan = scan;
    }


    public boolean isAuthService() {
        return isAuthService;
    }

    public void setIsAuthService(boolean authService) {
        isAuthService = authService;
    }
}
