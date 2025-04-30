package studio.clashbuddy.clashaccess.auth;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import studio.clashbuddy.clashaccess.gateway.GatewayMetadataHandler;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.lang.reflect.Method;


@Component
class AuthMetaDataAccessController implements ApplicationListener<ApplicationReadyEvent> {

    private final GatewayMetadataHandler gatewayMetadataHandler;
    private final RequestMappingHandlerMapping handlerMapping;
    private final ClashBuddySecurityClashAccessAppProperties props;
    private final Logger logger = LoggerFactory.getLogger(AuthMetaDataAccessController.class);
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    public AuthMetaDataAccessController(GatewayMetadataHandler gatewayMetadataHandler, RequestMappingHandlerMapping handlerMapping,
                                        ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.gatewayMetadataHandler = gatewayMetadataHandler;
        this.handlerMapping = handlerMapping;
        this.props = clashBuddySecurityClashAccessAppProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if(!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) return;
        if(!props.isAuthService()) return;
        if(!props.isAuthReceivePush()) return;
        String path = props.getAuthServiceEndpoint();
        String key = props.getAuthServiceKey();
        if (key ==null || path==null)
           throw new IllegalStateException("Please provide auth service endpoint and auth service key");

        RequestMappingInfo mappingInfo = RequestMappingInfo
                .paths(path)
                .methods(RequestMethod.POST)
                .build();
        HandlerMethod method = new HandlerMethod(gatewayMetadataHandler, findHandleMethod());
        handlerMapping.registerMapping(mappingInfo, gatewayMetadataHandler, method.getMethod());
    }

    private Method findHandleMethod() {
        try {
            return GatewayMetadataHandler.class.getMethod("handle", HttpServletRequest.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find handler method", e);
        }
    }
}
