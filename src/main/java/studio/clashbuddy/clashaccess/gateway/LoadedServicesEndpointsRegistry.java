package studio.clashbuddy.clashaccess.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import studio.clashbuddy.clashaccess.metadata.ClashScannedEndpointMetadata;
import studio.clashbuddy.clashaccess.properties.AccessCredential;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.util.*;

@Component
public class LoadedServicesEndpointsRegistry {
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    private final Map<String, ClashScannedEndpointMetadata> endpointMetadataHashMap = new HashMap<>();
    private final ClashBuddySecurityClashAccessGatewayProperties clashAccessGatewayProperties;
    private final Logger logger = LoggerFactory.getLogger(LoadedServicesEndpointsRegistry.class);

    public LoadedServicesEndpointsRegistry(ClashBuddySecurityClashAccessGatewayProperties clashAccessGatewayProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.clashAccessGatewayProperties = clashAccessGatewayProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        if(clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION))return;
        logger.info("✅ ClashAccess started in GATEWAY mode — loading metadata from configured services...");

        loadEndpointMetadata();
    }

    public boolean isPublicEndpoint(String endpoint) {
//        if (clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION))
//            throw new IllegalStateException(
//                    "❌ Invalid usage: this method is only intended for API Gateway services.\n" +
//                            "🔧 To fix this, set the correct service type in your configuration:\n\n" +
//                            "   clashbuddy.clashaccess.application.scan=true\n\n" +
//                            "🚫 Current context: 'application'\n" +
//                            "✅ Allowed context: 'gateway'"
//            );
//        ClashScannedEndpointMetadata metadata = endpointMetadataHashMap.get(endpoint);
//        if (metadata == null)
//            return true;
//        return metadata.isPublic();
        return false;
    }

    private void loadEndpointMetadata() {
        if(clashAccessGatewayProperties.getAccesses().isEmpty()) return;
        RestTemplate restTemplate = new RestTemplate();
        var accessType = clashAccessGatewayProperties.getAccessType().getAccessType();
        for (AccessCredential access : clashAccessGatewayProperties.getAccesses()) {
            String url = access.getEndpoint() + "?key=" + access.getKey() + "&p=" + accessType;
            try {
                List<ClashScannedEndpointMetadata> results = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<ClashScannedEndpointMetadata>>() {
                        }
                ).getBody();
                if (results == null) {
                    logger.error(
                            "❌ Failed to load metadata from service:\n" +
                                    "🔗 Endpoint: {}\n" +
                                    "🔑 Access Key: {}\n" +
                                    "📭 Response was null. Make sure the service is UP and responds with a JSON body of metadata.",
                            access.getEndpoint(),
                            access.getKey()
                    );

                    continue;
                }
                for (ClashScannedEndpointMetadata metadata : results) {
                    endpointMetadataHashMap.computeIfPresent(metadata.getMainEndpoint(), (k, v) -> {
                        String message = String.format(
                                "❌ ClashAccess Error: Duplicate endpoint detected during metadata load!\n\n" +
                                        "🔁 Endpoint: '%s'\n" +
                                        "🔒 Already registered by: %s (Service Key: %s)\n" +
                                        "🚨 Conflicting with: %s (Service Key: %s)\n\n" +
                                        "👉 Please ensure each service exposes unique main endpoints to avoid collision.",
                                k,
                                v.getFullControllerName(),
                                v.getContextPath(),
                                metadata.getFullControllerName(),
                                access.getKey()
                        );
                        throw new IllegalStateException(message);
                    });
                    endpointMetadataHashMap.put(metadata.getMainEndpoint(), metadata);
                }
            } catch (IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                logger.error(
                        "❌ Failed to fetch metadata from service:\n" +
                                "🔗 Endpoint: {}\n" +
                                "🔑 Access Key: {}\n" +
                                "💥 Reason: {}\n",
                        access.getEndpoint(),
                        access.getKey(),
                        e.getMessage()
                );
            }

        }
    }

}
