package studio.clashbuddy.clashaccess.metadata;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;



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
        String organize = request.getParameter("o");

        if(!clashBuddySecurityClashAccessAppProperties.getApiKey().equals(key))
            throw new ClashAccessDeniedException(
                    "❌ Access Denied: Invalid or missing ClashAccess metadata key.\n" +
                            "🔐 This endpoint requires a valid API key for metadata access.\n",
                    403
            );

        if(organize != null && organize.equalsIgnoreCase("org")) {
            return ResponseEntity.ok(scannedMetadataEndpoints.getOrganizedMetadataEndpoint());
        }

        return ResponseEntity.ok(scannedMetadataEndpoints.getMetaEndpoints());
    }
}