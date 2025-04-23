package studio.clashbuddy.clashaccess.metadata;

import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class ScannedMetadataEndpoints {

    private final Set<ClashScannedEndpointMetadata> endpoints = new HashSet<>();
    private boolean loaded = false;
    private final ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties;

    public ScannedMetadataEndpoints(ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties) {
        this.clashBuddySecurityClashAccessAppProperties = clashBuddySecurityClashAccessAppProperties;
    }

    public Set<ClashScannedEndpointMetadata> getMetaEndpoints() {
        if(!clashBuddySecurityClashAccessAppProperties.isScan())
            throw new IllegalStateException(
                    "❌ ClashAccess scanning is disabled.\n" +
                            "🔧 To enable endpoint scanning, set the following property in your configuration:\n\n" +
                            "    clashbuddy.clashaccess.application.scan=true\n\n" +
                            "📄 Location: application.yml or application.properties"
            );        return endpoints;
    }

    public Set<OrganizedEndpoints> getOrganizedMetadataEndpoint() {
        Map<String, List<ClashScannedEndpointMetadata>> organizedEndpointsGroups = getMetaEndpoints().stream().collect(Collectors.groupingBy(ClashScannedEndpointMetadata::getFullControllerName));
        Set<OrganizedEndpoints> organizedEndpointsSet = new HashSet<>();
        organizedEndpointsGroups.forEach((key, value) -> {
            OrganizedEndpoints organizedEndpoints = new OrganizedEndpoints(key, value);
            organizedEndpointsSet.add(organizedEndpoints);
        });
        return organizedEndpointsSet;
    }

    void setEndpoints(Set<ClashScannedEndpointMetadata> endpoints) {
        if (loaded) return;
        this.endpoints.addAll(endpoints);
        loaded = true;
    }




}
