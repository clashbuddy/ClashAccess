package studio.clashbuddy.clashaccess;

import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.metadata.ClashAccessMetadataAware;
import studio.clashbuddy.clashaccess.metadata.ClashScannedEndpointMetadata;

import java.util.Set;

@Component
public class MyMetadataLogger extends ClashAccessMetadataAware {


    @Override
    protected void onMetadataReady(Set<ClashScannedEndpointMetadata> metadata) {
        System.out.println(metadata);
    }
}
