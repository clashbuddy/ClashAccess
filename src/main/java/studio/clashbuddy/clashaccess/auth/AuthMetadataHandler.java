package studio.clashbuddy.clashaccess.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.gateway.MetadataPayload;
import studio.clashbuddy.clashaccess.metadata.ClashScannedEndpointMetadata;
import studio.clashbuddy.clashaccess.metadata.MetadataRefreshEvent;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthMetadataHandler {
    private final ClashBuddySecurityClashAccessAppProperties properties;
    @Autowired
    private ApplicationEventPublisher publisher;
    public AuthMetadataHandler(ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties) {
        this.properties = clashBuddySecurityClashAccessAppProperties;
    }


    public ResponseEntity<Object> handle(HttpServletRequest request) {
        String key = request.getParameter("key");
        if(!properties.getAuthServiceKey().equals(key))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( "❌ Access Denied: Invalid or missing ClashAccess Auth metadata key.\n" +
                    "🔐 This endpoint requires a valid API key for metadata access.\n");

        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            Set<ClashScannedEndpointMetadata> metadata =  mapper.readValue(
                    body,
                    new TypeReference<>() {
                    }
            );
            publisher.publishEvent(new MetadataRefreshEvent(metadata));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authservice: "+e.getMessage());
        }
        return ResponseEntity.accepted().body("Gateway received your metadata");
    }
}
