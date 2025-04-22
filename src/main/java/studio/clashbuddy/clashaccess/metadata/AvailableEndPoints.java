package studio.clashbuddy.clashaccess.metadata;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class AvailableEndPoints {

    private final Set<SecuredEndpointMetadata> endpoints = new HashSet<>();
    private boolean loaded = false;

    public Set<SecuredEndpointMetadata> getMetaEndpoints() {
        return endpoints;
    }

    void setEndpoints(Set<SecuredEndpointMetadata> endpoints) {
        if (loaded) return;
        this.endpoints.addAll(endpoints);
        loaded = true;
    }


}
