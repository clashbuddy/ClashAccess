package studio.clashbuddy.clashaccess.metadata;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import studio.clashbuddy.clashaccess.gateway.EndpointMeta;
import studio.clashbuddy.clashaccess.gateway.EndpointReadHelper;
import studio.clashbuddy.clashaccess.gateway.MetadataPayload;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.security.config.AccessRules;
import studio.clashbuddy.clashaccess.security.config.ProtectedRule;

import java.util.*;
import java.util.stream.Collectors;

@Component
class ScannedMetadataEndpoints {

    private final Set<ClashScannedEndpointMetadata> endpoints = new HashSet<>();
    private boolean loaded = false;
    private final ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties;
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    public ScannedMetadataEndpoints(ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.clashBuddySecurityClashAccessAppProperties = clashBuddySecurityClashAccessAppProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
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

    MetadataPayload getMetadataPayload(ClashBuddySecurityClashAccessGatewayProperties.AccessType accessType) {
        if(accessType.equals(ClashBuddySecurityClashAccessGatewayProperties.AccessType.PUBLIC))
            return loadPublicMetadataPayload();
        if(accessType.equals(ClashBuddySecurityClashAccessGatewayProperties.AccessType.PRIVATE))
            return loadPrivateMetadataPayload();
        return loadPublicMetadataPayload();
    }

    private MetadataPayload loadPrivateMetadataPayload() {
        List<EndpointMeta> privateEndpoints = new ArrayList<>();
        for(ClashScannedEndpointMetadata endpointMetadata : endpoints) {
            List<String> privateEnds = EndpointReadHelper.privateEndpoints(endpointMetadata);
            if(!privateEnds.isEmpty()){
                var methods = EndpointReadHelper.privateMethods(endpointMetadata);
                privateEndpoints.add(new EndpointMeta(privateEnds, methods));
            }
        }
        return new MetadataPayload(clashBuddyClashAccessProperties.getServiceId(),privateEndpoints);
    }


    private MetadataPayload loadPublicMetadataPayload() {
        List<EndpointMeta> publicEndpoints = new ArrayList<>();
        for(ClashScannedEndpointMetadata endpointMetadata : endpoints) {
            if(endpointMetadata.getPublicEndpoints() != null && endpointMetadata.getPublicEndpoints().length != 0)
                publicEndpoints.add(new EndpointMeta(Arrays.asList(endpointMetadata.getPublicEndpoints()), Arrays.asList(endpointMetadata.getPublicHttpMethods())));
        }
        return new MetadataPayload(clashBuddyClashAccessProperties.getServiceId(), publicEndpoints);
    }



    void setEndpoints(Set<ClashScannedEndpointMetadata> endpoints) {
        if (loaded) return;
        this.endpoints.addAll(endpoints);
        loaded = true;
    }

    void changeEndPointsToPrivate(AccessRules accessRules) {
        if (accessRules == null || accessRules.getProtectedRules().isEmpty() || endpoints == null) {
            return;
        }

        AntPathMatcher antPathMatcher =new AntPathMatcher();

        // Precompute: Build a map from path -> rules fast access (optional)
        Set<ProtectedRule> accessRuleSet = accessRules.getProtectedRules();

        for (var endpoint : endpoints) {
            Set<String> endpointPaths = new HashSet<>(Arrays.asList(endpoint.getEndpoints()));


            for (var rule : accessRuleSet) {
                Set<String> rulePaths = rule.getPaths();
                if(rulePaths.stream().anyMatch(a-> endpointPaths.stream().anyMatch(b-> antPathMatcher.match(a,b)))) {
                    var filtered = MetadataMapFilter.filterMetadata(endpointPaths, new HashSet<>(Arrays.asList(endpoint.getHttpMethods())), rulePaths, new HashSet<>(Arrays.asList(rule.getMethods())),antPathMatcher);
                    endpoint.changePublicEndpoints(filtered.get("keepEndpoints"));
                    endpoint.changePublicMethods(filtered.get("keepMethods"));
                }

            }
        }
    }




}
