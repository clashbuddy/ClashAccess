package studio.clashbuddy.clashaccess.metadata;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;


@Component
class EndpointMetadataHandler {
    private final ScannedMetadataEndpoints scannedMetadataEndpoints;
    private final ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties;

    public EndpointMetadataHandler(ScannedMetadataEndpoints scannedMetadataEndpoints, ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties) {
        this.scannedMetadataEndpoints = scannedMetadataEndpoints;
        this.clashBuddySecurityClashAccessAppProperties = clashBuddySecurityClashAccessAppProperties;
    }

    public ResponseEntity<Object> handle(HttpServletRequest request) {
        String key = request.getParameter("key");
        String service = request.getParameter("service");
        String s = request.getParameter("s");
        if(!clashBuddySecurityClashAccessAppProperties.getApiKey().equals(key))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( "❌ Access Denied: Invalid or missing ClashAccess Application metadata key.\n" +
                    "🔐 This endpoint requires a valid API key for metadata access.\n");
        if(s != null && s.equals("s")){
            if(!clashBuddySecurityClashAccessAppProperties.isAuthService())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( "This service is not auth service");
            if(clashBuddySecurityClashAccessAppProperties.getAuthServiceSecret() == null || clashBuddySecurityClashAccessAppProperties.getAuthServiceSecret().isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( "Auth Service Secret is empty");
            return ResponseEntity.ok(clashBuddySecurityClashAccessAppProperties.getAuthServiceSecret());
        }
        if(service != null && service.equalsIgnoreCase("auth-service"))
            return ResponseEntity.ok(scannedMetadataEndpoints.getMetaEndpoints());
        var type = ClashBuddySecurityClashAccessGatewayProperties.AccessType.fromAccessType(request.getParameter("type"));
        return ResponseEntity.ok(scannedMetadataEndpoints.getMetadataPayload(type));
    }
}