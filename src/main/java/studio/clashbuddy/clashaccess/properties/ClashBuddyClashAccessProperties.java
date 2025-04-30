package studio.clashbuddy.clashaccess.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess")
public class ClashBuddyClashAccessProperties {
    private ServiceType serviceType = ServiceType.APPLICATION;
    private String serviceId;

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceId() {
        if(serviceId == null) {
            return "Unknown-service-"+ UUID.randomUUID();
        }
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
