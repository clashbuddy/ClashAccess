package studio.clashbuddy.clashaccess.gateway;

import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

@Service
public class ClashAccessGatewayEndpointServicesManager {
    private final LoadedServicesEndpointsRegistry loadedServicesEndpointsRegistry;
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public ClashAccessGatewayEndpointServicesManager(LoadedServicesEndpointsRegistry loadedServicesEndpointsRegistry, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.loadedServicesEndpointsRegistry = loadedServicesEndpointsRegistry;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }

    void pushServiceMetadata(MetadataPayload payload, ClashBuddySecurityClashAccessGatewayProperties.AccessType type) {
        if(type.equals(ClashBuddySecurityClashAccessGatewayProperties.AccessType.PUBLIC)) {
            loadedServicesEndpointsRegistry.getPublicEndpoints().put(payload.getId(), payload);
        }else{
            loadedServicesEndpointsRegistry.getPrivateEndpoints().put(payload.getId(), payload);
        }
    }

    public boolean isPublicEndpoint(String endpoint, String method, String serviceId) {
        rejectIfNotGateway();
        MetadataPayload metadataPayload =loadedServicesEndpointsRegistry.getPublicEndpoints().get(serviceId);
        if(metadataPayload == null)
            return false;
        for(EndpointMeta endpointMeta : metadataPayload.getEndpoints())
            for(String end : endpointMeta.getEndpoints())
                if(pathMatcher.match(end, endpoint))
                    return endpointMeta.getMethods().contains(method);
        return false;
    }

    public boolean isPrivateEndpoint(String endpoint, String method, String serviceId) {
        rejectIfNotGateway();
        MetadataPayload metadataPayload =loadedServicesEndpointsRegistry.getPrivateEndpoints().get(serviceId);
        if(metadataPayload == null)
            return false;
        for(EndpointMeta endpointMeta : metadataPayload.getEndpoints())
            for(String end : endpointMeta.getEndpoints())
                if(pathMatcher.match(end, endpoint))
                    return endpointMeta.getMethods().contains(method);
        return false;
    }

    private void rejectIfNotGateway() {
        if (!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.GATEWAY))
            throw new IllegalStateException(
                    "❌ Invalid usage: this method is only intended for API Gateway services.\n" +
                            "🔧 To fix this, set the correct service type in your configuration:\n\n" +
                            "   clashbuddy.clashaccess.application.scan=true\n\n" +
                            "🚫 Current context: 'application'\n" +
                            "✅ Allowed context: 'gateway'"
            );
    }

}
