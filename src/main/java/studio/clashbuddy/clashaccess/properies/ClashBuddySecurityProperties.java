package studio.clashbuddy.clashaccess.properies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.security")

public class ClashBuddySecurityProperties {
    private String endpointMetadata;
    private String apiKey;
    private boolean isClient = true;
    private boolean enabled = false;
    private boolean scan=true;

    public ClashBuddySecurityProperties() {
        endpointMetadata = getDefaultEndpoint();
        apiKey = getDefaultApiKey();
    }

    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean client) {
        isClient = client;
    }

    public String getApiKey() {

        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEndpointMetadata() {

        return endpointMetadata;
    }

    public boolean isNotChanged() {

        return endpointMetadata.equalsIgnoreCase(getEndpointMetadata()) && apiKey.equalsIgnoreCase(getApiKey());
    }

    public String getDefaultEndpoint() {
        return "/clashbuddy-clash-access/endpoint-metadata";
    }

    public String getDefaultApiKey() {
        return "access";
    }

    public void setDefaults(){
        endpointMetadata = getDefaultEndpoint();
        apiKey = getDefaultApiKey();
    }

    public void setEndpointMetadata(String endpointMetadata) {
        this.endpointMetadata = endpointMetadata;
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
