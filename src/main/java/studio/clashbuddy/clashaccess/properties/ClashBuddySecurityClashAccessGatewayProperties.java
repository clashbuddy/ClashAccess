package studio.clashbuddy.clashaccess.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess.gateway")
public class ClashBuddySecurityClashAccessGatewayProperties {
    private AccessType accessType = AccessType.PUBLIC;
    private List<AccessCredential> accesses = new ArrayList<>();

    public AccessType getAccessType() {
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

    public enum AccessType{
        PRIVATE("pr"),PUBLIC("pb"),BOTH("pp");
        private final String accessType;
        AccessType(String accessType) {
            this.accessType = accessType;
        }
        public String getAccessType() {
            return accessType;
        }
    }

}
