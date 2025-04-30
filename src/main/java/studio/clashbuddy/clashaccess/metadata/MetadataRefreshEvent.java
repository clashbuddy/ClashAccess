package studio.clashbuddy.clashaccess.metadata;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

public class MetadataRefreshEvent extends ApplicationEvent {
 private final Set<ClashScannedEndpointMetadata> metadata;

 public MetadataRefreshEvent(Set<ClashScannedEndpointMetadata> metadata) {
     super(metadata);
     this.metadata = metadata;
 }

 public Set<ClashScannedEndpointMetadata> getMetadata() {
  return metadata;
 }
}