package studio.clashbuddy.clashaccess.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.util.Set;

public abstract class ClashAccessMetadataAware implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ScannedMetadataEndpoints metadataEndpoints;
    @Autowired
    private ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;

    private boolean metadataHandled = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) return;


        if (!metadataHandled) {
            metadataHandled = true;
            onMetadataReady(metadataEndpoints.getMetaEndpoints());
        }
    }

    protected abstract void onMetadataReady(Set<ClashScannedEndpointMetadata> metadata);
}
