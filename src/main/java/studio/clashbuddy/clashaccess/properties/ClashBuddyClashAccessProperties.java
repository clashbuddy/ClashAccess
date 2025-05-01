package studio.clashbuddy.clashaccess.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess")
public class ClashBuddyClashAccessProperties {
    private String serviceId;

    public String getServiceId() {
        if(serviceId == null) {
            serviceId = "Unknown-service-"+ UUID.randomUUID();
        }
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
