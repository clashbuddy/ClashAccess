package studio.clashbuddy.clashaccess.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.util.Set;

public abstract class ClashAccessMetadataAware implements ApplicationListener<MetadataRefreshEvent> {

    @Autowired
    private ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;

    @Override
    public void onApplicationEvent(MetadataRefreshEvent event) {
        if (!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) return;
        onMetadata(event.getMetadata());
    }

    protected abstract void onMetadata(Set<ClashScannedEndpointMetadata> metadata);

}
