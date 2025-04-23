package studio.clashbuddy.clashaccess.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "clashbuddy.clashaccess")
public class ClashBuddyClashAccessProperties {
    private ServiceType serviceType = ServiceType.APPLICATION;


    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }


}
