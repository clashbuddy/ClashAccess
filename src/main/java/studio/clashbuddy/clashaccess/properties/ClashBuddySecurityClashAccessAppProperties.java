package studio.clashbuddy.clashaccess.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.application")
public class ClashBuddySecurityClashAccessAppProperties {
    private AccessCredential access = new AccessCredential();
    private boolean enabled = true;
    private boolean scan = true;

    public ClashBuddySecurityClashAccessAppProperties() {
        setDefaults();
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







}
