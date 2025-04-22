package studio.clashbuddy.clashaccess.metadata;


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
import studio.clashbuddy.clashaccess.properies.ClashBuddySecurityProperties;

import java.lang.reflect.Method;

@Component
public class MetaDataAccessController implements ApplicationListener<ApplicationReadyEvent> {

    private final RequestMappingHandlerMapping handlerMapping;
    private final EndpointMetadataHandler handler;
    private final ClashBuddySecurityProperties props;
    Logger logger = LoggerFactory.getLogger(ClashBuddySecurityProperties.class);
    public MetaDataAccessController(RequestMappingHandlerMapping handlerMapping,
                                    EndpointMetadataHandler handler,
                                    ClashBuddySecurityProperties props) {
        this.handlerMapping = handlerMapping;
        this.handler = handler;
        this.props = props;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String path = props.getEndpointMetadata();
        String key = props.getApiKey();
        if(!props.isEnabled()) return;
        if(props.isNotChanged())
            logger.warn("Using endpoint-metadata which is not have secure API-KEY: 'access'. please change this clashbuddy.clashaccess.security.api-key in application .yml/properties ");

        if(!StringUtils.hasText(key) || !StringUtils.hasText(path)) {
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
            return EndpointMetadataHandler.class.getMethod("handle");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find handler method", e);
        }
    }
}
