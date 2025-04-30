package studio.clashbuddy.clashaccess.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.pushendpoint")
public class PushProperties {

    private String gatewayEndpoint;
    private String gatewayKey;
    private String authServiceEndpoint;
    private String authServiceKey;

    public String getGatewayEndpoint() {
        return gatewayEndpoint;
    }

    public void setGatewayEndpoint(String gatewayEndpoint) {
        this.gatewayEndpoint = gatewayEndpoint;
    }

    public String getGatewayKey() {
        return gatewayKey;
    }

    public void setGatewayKey(String gatewayKey) {
        this.gatewayKey = gatewayKey;
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
}
