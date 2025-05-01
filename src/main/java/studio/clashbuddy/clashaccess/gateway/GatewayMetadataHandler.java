package studio.clashbuddy.clashaccess.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.auth.SecretStorage;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;

import java.io.IOException;
import java.util.stream.Collectors;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Component
public class GatewayMetadataHandler {

    private final ClashBuddySecurityClashAccessGatewayProperties properties;
    private final ClashAccessGatewayEndpointServicesManager endpointServicesManager;
    private final SecretStorage secretStorage;
    public GatewayMetadataHandler(ClashBuddySecurityClashAccessGatewayProperties clashBuddySecurityClashAccessGatewayProperties, ClashAccessGatewayEndpointServicesManager endpointServicesManager, SecretStorage secretStorage) {
        this.properties = clashBuddySecurityClashAccessGatewayProperties;
        this.endpointServicesManager = endpointServicesManager;
        this.secretStorage = secretStorage;
    }

    public ResponseEntity<Object> handle(HttpServletRequest request) {
        String key = request.getParameter("key");
        String s = request.getParameter("s");
        var type = ClashBuddySecurityClashAccessGatewayProperties.AccessType.fromAccessType(request.getParameter("p"));

        if(!properties.getKey().equals(key))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( "❌ Access Denied: Invalid or missing ClashAccess Gateway metadata key.\n" +
                    "🔐 This endpoint requires a valid API key for metadata access.\n");
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if(s != null &&s.equals("s")){
                secretStorage.setSecret(body);
                return ResponseEntity.status(HttpStatus.OK).body("SecretKey is received successfully.");
            }

            ObjectMapper mapper = new ObjectMapper();
            MetadataPayload metadata = mapper.readValue(body, MetadataPayload.class);
            endpointServicesManager.pushServiceMetadata(metadata,type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return ResponseEntity.accepted().body("Gateway received your metadata");
    }

}
