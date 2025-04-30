package studio.clashbuddy.clashaccess.gateway;


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
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.lang.reflect.Method;


@Component
class GatewayMetaDataAccessController implements ApplicationListener<ApplicationReadyEvent> {

    private final GatewayMetadataHandler gatewayMetadataHandler;
    private final RequestMappingHandlerMapping handlerMapping;
    private final ClashBuddySecurityClashAccessGatewayProperties props;
    private final Logger logger = LoggerFactory.getLogger(GatewayMetaDataAccessController.class);
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    public GatewayMetaDataAccessController(GatewayMetadataHandler gatewayMetadataHandler, RequestMappingHandlerMapping handlerMapping,
                                           ClashBuddySecurityClashAccessGatewayProperties clashBuddySecurityClashAccessGatewayProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.gatewayMetadataHandler = gatewayMetadataHandler;
        this.handlerMapping = handlerMapping;
        this.props = clashBuddySecurityClashAccessGatewayProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if(!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.GATEWAY)) return;
        if(!props.isEnableEndpoint()) return;
        String path = props.getEndpoint();
        String key = props.getKey();
        if (key.equalsIgnoreCase("access"))
            logger.warn(
                    "⚠️ ClashAccess Gateway metadata endpoint is using the default insecure API key: 'access'.\n" +
                            "🔐 Please update it by setting a strong key in your configuration:\n\n" +
                            "    clashbuddy.clashaccess.gateway.key=your-secure-key\n\n" +
                            "📄 Location: application.yml or application.properties"
            );
        if (!StringUtils.hasText(key) || !StringUtils.hasText(path))
            throw new IllegalStateException("Please provide endpoint and key");

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
