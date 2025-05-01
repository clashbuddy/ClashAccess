package studio.clashbuddy.clashaccess.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import studio.clashbuddy.clashaccess.helpers.MetadataPayload;
import studio.clashbuddy.clashaccess.properties.*;
import studio.clashbuddy.clashaccess.utils.AccessType;

import java.net.URI;
import java.util.Set;


@Service
public class PushMetadataService {
    private final Logger log = LoggerFactory.getLogger(PushMetadataService.class);
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    private final ClashBuddySecurityClashAccessAppProperties appProperties;
    private final PushProperties pushProperties;
    private final ScannedMetadataEndpoints scannedMetadataEndpoints;
    public PushMetadataService(ClashBuddyClashAccessProperties clashBuddyClashAccessProperties, ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties,
                               PushProperties pushProperties, ScannedMetadataEndpoints scannedMetadataEndpoints) {
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;

        this.appProperties = clashBuddySecurityClashAccessAppProperties;
        this.pushProperties = pushProperties;
        this.scannedMetadataEndpoints = scannedMetadataEndpoints;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {

           if(appProperties.isEnabled()){
               String gatewayEndpoint = pushProperties.getGatewayEndpoint();
               String gatewayKey = pushProperties.getGatewayKey();
               String authEndpoint = pushProperties.getAuthServiceEndpoint();
               String authKey = pushProperties.getAuthServiceKey();
               RestTemplate restTemplate = new RestTemplate();
               if(gatewayEndpoint != null && gatewayKey != null)
                   pushToGateway(gatewayEndpoint, gatewayKey,restTemplate);
               if(!appProperties.isAuthService())
                   if(authEndpoint != null && authKey != null)
                       pushToAuthService(authEndpoint, authKey,restTemplate);
               if(appProperties.isAuthService())
                   pushToGatewayAuthSecretKey(gatewayEndpoint, gatewayKey,restTemplate);
           }
    }

    private void pushToGatewayAuthSecretKey(String url,String key,RestTemplate restTemplate){
        url+= (url.contains("?") ? "&key=" +key : "?key=" + key)+"&s=s";
        if(!appProperties.isShareAuthSecretKey())
            log.warn("Auth service secret is not shared to api gateway");
        if(appProperties.getAuthServiceSecret() ==null || appProperties.getAuthServiceSecret().isEmpty())
            log.warn("Auth service secret is empty");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(appProperties.getAuthServiceSecret(), headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            log.info("Push Gateway Auth Service secret response: {}", response);
        }catch (Exception e){
            log.error("Error while pushing secret to gateway {}", e.getMessage());
        }
    }

    private void pushToGateway(String url,String key,RestTemplate restTemplate){
        url+= url.contains("?") ? "&key=" +key : "?key=" + key;
        URI uri = URI.create(url);
        UriComponents components = UriComponentsBuilder.fromUri(uri).build();
        var p = AccessType.fromAccessType(components.getQueryParams().getFirst("p"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MetadataPayload requestBody = scannedMetadataEndpoints.getMetadataPayload(p);
        HttpEntity<MetadataPayload> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            log.info("Push Gateway Metadata response: {}", response);
        }catch (Exception e){
            log.error("Error while pushing metadata to gateway {}", e.getMessage());
        }
    }

    private void pushToAuthService(String url,String key,RestTemplate restTemplate){
        url+= url.contains("?") ? "&key=" +key : "?key=" + key;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Set<ClashScannedEndpointMetadata> requestBody = scannedMetadataEndpoints.getMetaEndpoints();
        HttpEntity<Set<ClashScannedEndpointMetadata>> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            log.info("Push AuthService Metadata response: {}", response);
        }catch (Exception e){
            log.error("Error while pushing metadata to authService {}", e.getMessage());
        }
    }

}
