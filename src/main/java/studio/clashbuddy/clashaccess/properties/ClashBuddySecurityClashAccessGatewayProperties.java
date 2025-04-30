package studio.clashbuddy.clashaccess.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.gateway")
public class ClashBuddySecurityClashAccessGatewayProperties {
    private AccessType accessType = AccessType.PUBLIC;
    private String endpoint = "/clashbuddy-clash-access/gateway-notify";
    private String key = "access";

    private boolean enableEndpoint = true;
    private List<AccessCredential> accesses = new ArrayList<>();

    private AccessCredential authServiceAccess;

    public AccessCredential getAuthServiceAccess() {
        return authServiceAccess;
    }

    public void setAuthServiceAccess(AccessCredential authServiceAccess) {
        this.authServiceAccess = authServiceAccess;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getKey() {
        return key;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AccessType getAccessType() {
        if(accessType == null)
            return AccessType.PUBLIC;
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public List<AccessCredential> getAccesses() {
        return accesses;
    }

    public void setAccesses(List<AccessCredential> accesses) {
        this.accesses = accesses;
    }

    public boolean isEnableEndpoint() {
        return enableEndpoint;
    }

    public void setEnableEndpoint(boolean enableEndpoint) {
        this.enableEndpoint = enableEndpoint;
    }

    public enum AccessType{
        PRIVATE("pr"),PUBLIC("pb");
        private final String accessType;
        AccessType(String accessType) {
            this.accessType = accessType;
        }
        public String getAccessType() {
            return accessType;
        }
        public static AccessType fromAccessType(String accessType) {
            for (AccessType type : AccessType.values()) {
                if (type.accessType.equals(accessType)) {
                    return type;
                }
            }
            return AccessType.PUBLIC;
        }
    }

}
