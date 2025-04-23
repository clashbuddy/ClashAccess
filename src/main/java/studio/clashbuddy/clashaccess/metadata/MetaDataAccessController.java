package studio.clashbuddy.clashaccess.metadata;


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
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;

import java.lang.reflect.Method;


@Component
class MetaDataAccessController implements ApplicationListener<ApplicationReadyEvent> {


    private final RequestMappingHandlerMapping handlerMapping;
    private final EndpointMetadataHandler handler;
    private final ClashBuddySecurityClashAccessAppProperties props;
    private final Logger logger = LoggerFactory.getLogger(MetaDataAccessController.class);
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    public MetaDataAccessController(RequestMappingHandlerMapping handlerMapping,
                                    EndpointMetadataHandler handler,
                                    ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {

        this.handlerMapping = handlerMapping;
        this.handler = handler;
        this.props = clashBuddySecurityClashAccessAppProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if(!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) return;

        String path = props.getEndpointMetadata();
        String key = props.getApiKey();
        if (!props.isEnabled()) return;
        if (props.isNotChanged())
            logger.warn(
                    "⚠️ ClashAccess metadata endpoint is using the default insecure API key: 'access'.\n" +
                            "🔐 Please update it by setting a strong key in your configuration:\n\n" +
                            "    clashbuddy.clashaccess.application.access.key=your-secure-key\n\n" +
                            "📄 Location: application.yml or application.properties"
            );
        if (!StringUtils.hasText(key) || !StringUtils.hasText(path)) {
            path = props.getDefaultEndpoint();
            key = props.getDefaultApiKey();
            props.setDefaults();
        }

        RequestMappingInfo mappingInfo = RequestMappingInfo
                .paths(path)
                .methods(RequestMethod.GET)
                .build();

        HandlerMethod method = new HandlerMethod(handler, findHandleMethod());
        handlerMapping.registerMapping(mappingInfo, handler, method.getMethod());
    }

    private Method findHandleMethod() {
        try {
            return EndpointMetadataHandler.class.getMethod("handle", HttpServletRequest.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find handler method", e);
        }
    }
}
