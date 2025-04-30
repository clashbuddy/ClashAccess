package studio.clashbuddy.clashaccess.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import studio.clashbuddy.clashaccess.metadata.ClashScannedEndpointMetadata;
import studio.clashbuddy.clashaccess.metadata.MetadataRefreshEvent;
import studio.clashbuddy.clashaccess.properties.AccessCredential;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.util.HashSet;
import java.util.Set;

@Component
class LoadServicesMetadataEndpoints {

    private final ClashBuddyClashAccessProperties properties;
    private final ClashBuddySecurityClashAccessAppProperties appProperties;
    private final Logger logger = LoggerFactory.getLogger(LoadServicesMetadataEndpoints.class);

    private final ApplicationEventPublisher publisher;
    LoadServicesMetadataEndpoints(ClashBuddyClashAccessProperties clashBuddyClashAccessProperties, ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ApplicationEventPublisher publisher) {
        this.properties = clashBuddyClashAccessProperties;
        this.appProperties = clashBuddySecurityClashAccessAppProperties;
        this.publisher = publisher;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        if(properties.getServiceType().equals(ServiceType.APPLICATION))return;
        if(!appProperties.isAuthService()) return;
        if(appProperties.getServiceEps().isEmpty()) return;
        loadServicesMetadata();
        logger.info("Clash access auth service metadata loading from other service completed");
    }


    private void loadServicesMetadata() {
        RestTemplate restTemplate = new RestTemplate();
        Set<ClashScannedEndpointMetadata> scannedEndpointMetadata = new HashSet<>();
        for (AccessCredential accessCredential : appProperties.getServiceEps()){
            String url = accessCredential.getEndpoint();
            String key = accessCredential.getKey();
            try{
                var response = restTemplate.exchange(url+"?key="+key, HttpMethod.GET, HttpEntity.EMPTY,   new ParameterizedTypeReference<Set<ClashScannedEndpointMetadata>>() {});

                if(response.getStatusCode().is2xxSuccessful()){
                    if(response.getBody() != null){
                        scannedEndpointMetadata.addAll(response.getBody());
                    }else {
                        logger.info("No scanned endpoints found");
                    }
                }

            }catch (Exception e){
                logger.error("AuthService Error while loading service metadata {}", e.getMessage());
            }
        }
        publisher.publishEvent(new MetadataRefreshEvent(scannedEndpointMetadata));
    }




}
