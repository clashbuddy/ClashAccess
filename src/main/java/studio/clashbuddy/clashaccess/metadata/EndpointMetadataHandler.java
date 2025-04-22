package studio.clashbuddy.clashaccess.metadata;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class EndpointMetadataHandler {
    private final AvailableEndPoints availableEndPoints;

    public EndpointMetadataHandler(AvailableEndPoints availableEndPoints) {
        this.availableEndPoints = availableEndPoints;
    }

    public ResponseEntity<Set<SecuredEndpointMetadata>> handle() {
        return ResponseEntity.ok(availableEndPoints.getMetaEndpoints());
    }
}