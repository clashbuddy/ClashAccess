package studio.clashbuddy.clashaccess.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import studio.clashbuddy.clashaccess.auth.SecretStorage;
import studio.clashbuddy.clashaccess.properties.AccessCredential;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.util.*;

@Component
public class LoadedServicesEndpointsRegistry {
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    private final Map<String, MetadataPayload> privateEndpoints = new HashMap<>();
    private final Map<String, MetadataPayload> publicEndpoints = new HashMap<>();
    private final ClashBuddySecurityClashAccessGatewayProperties clashAccessGatewayProperties;
    private final Logger logger = LoggerFactory.getLogger(LoadedServicesEndpointsRegistry.class);
    private final SecretStorage secretStorage;
    public LoadedServicesEndpointsRegistry(ClashBuddySecurityClashAccessGatewayProperties clashBuddySecurityClashAccessGatewayProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties, SecretStorage secretStorage) {
        this.clashAccessGatewayProperties = clashBuddySecurityClashAccessGatewayProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
        this.secretStorage = secretStorage;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        if(clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION))return;
        logger.info("✅ ClashAccess started in GATEWAY mode — loading metadata from configured services...");

        loadEndpointMetadata();
        loadAuthServicesJwtSecretKey();
    }

    public Map<String, MetadataPayload> getPrivateEndpoints() {
        return privateEndpoints;
    }

    public Map<String, MetadataPayload> getPublicEndpoints() {
        return publicEndpoints;
    }

    private void loadAuthServicesJwtSecretKey(){
        var access = clashAccessGatewayProperties.getAuthServiceAccess();
        if(access == null) return;
        RestTemplate restTemplate = new RestTemplate();
        String url = access.getEndpoint()+"?key="+access.getKey()+"&s=s";
        try {
            var responseEntity  = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()){
                secretStorage.setSecret(responseEntity.getBody());
            }else{
                throw new Exception(responseEntity.getBody());
            }
        }catch (Exception e){
            logger.error("Gateway cannot load auth service access key {}",e.getMessage());
        }
    }

    private void loadEndpointMetadata() {
        if(clashAccessGatewayProperties.getAccesses().isEmpty()) return;
        RestTemplate restTemplate = new RestTemplate();
        var accessType = clashAccessGatewayProperties.getAccessType().getAccessType();
        for (AccessCredential access : clashAccessGatewayProperties.getAccesses()) {
            String url = access.getEndpoint() + "?key=" + access.getKey() + "&p=" + accessType;
            try {
                var responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        MetadataPayload.class
                );
                if(!responseEntity.getStatusCode().is2xxSuccessful())
                    throw new Exception(String.valueOf(responseEntity.getBody()));
                var metadataPayload = responseEntity.getBody();
                if (metadataPayload == null) {
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
                if(clashAccessGatewayProperties.getAccessType().equals(ClashBuddySecurityClashAccessGatewayProperties.AccessType.PUBLIC)) {
                    publicEndpoints.put(metadataPayload.getId(), metadataPayload);
                } else if (clashAccessGatewayProperties.getAccessType().equals(ClashBuddySecurityClashAccessGatewayProperties.AccessType.PRIVATE)) {
                    privateEndpoints.put(metadataPayload.getId(), metadataPayload);
                }
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
