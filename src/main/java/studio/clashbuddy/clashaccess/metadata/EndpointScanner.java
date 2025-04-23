package studio.clashbuddy.clashaccess.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;
import studio.clashbuddy.clashaccess.security.RequireAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class EndpointScanner implements ApplicationListener<ApplicationReadyEvent> {

    private final ScannedMetadataEndpoints endPoints;
    private final ClashBuddySecurityClashAccessAppProperties securityProperties;
    @Value("${server.servlet.context-path:/}")
    private String prefixPath;
    private final ClashBuddyClashAccessProperties clashBuddyClashAccessProperties;
    private final Logger logger = LoggerFactory.getLogger(EndpointScanner.class);


    public EndpointScanner(ScannedMetadataEndpoints endPoints, ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.endPoints = endPoints;
        this.securityProperties = clashBuddySecurityClashAccessAppProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if(!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) return;
        String scanStatus = securityProperties.isScan()
                ? "📦 Ready to process metadata endpoints."
                : "⚠️  Endpoint scanning is disabled. No endpoints will be processed.";

        logger.info(
                "\n" +
                        "✅ ClashAccess Initialization Summary:\n" +
                        "   🔧 Mode:              APPLICATION\n" +
                        "   🔗 Endpoint :         {}\n" +
                        "   🔍 Endpoint Scan:     {}\n" +
                        "   ✅ Module Enabled:     {}\n" +
                        "   {}\n",
                securityProperties.getEndpointMetadata(),
                securityProperties.isScan(),
                securityProperties.isEnabled(),
                scanStatus
        );

        if (!securityProperties.isScan()) return;
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> controllers = context.getBeansWithAnnotation(RestController.class);
        Set<ClashScannedEndpointMetadata> securedEndpoints = new HashSet<>();
        for (Object bean : controllers.values()) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            String basePath = Optional.ofNullable(clazz.getAnnotation(RequestMapping.class))
                    .map(a -> a.value().length > 0 ? a.value()[0] : "")
                    .orElse("");

            for (Method method : clazz.getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) continue;

                RequireAccess access = method.getAnnotation(RequireAccess.class);
                boolean isPublic = access == null;

                String[] methodPath = new String[0];
                RequestMethod httpMethod = RequestMethod.GET;

                if (method.isAnnotationPresent(GetMapping.class)) {
                    methodPath = method.getAnnotation(GetMapping.class).value();
                    httpMethod = RequestMethod.GET;
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    methodPath = method.getAnnotation(PostMapping.class).value();
                    httpMethod = RequestMethod.POST;
                } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                    methodPath = method.getAnnotation(DeleteMapping.class).value();
                    httpMethod = RequestMethod.DELETE;
                }
                else if (method.isAnnotationPresent(PutMapping.class)) {
                    methodPath = method.getAnnotation(PutMapping.class).value();
                    httpMethod = RequestMethod.PUT;
                }
                else if (method.isAnnotationPresent(PatchMapping.class)) {
                    methodPath = method.getAnnotation(PatchMapping.class).value();
                    httpMethod = RequestMethod.PATCH;
                } else if (method.isAnnotationPresent(RequestMapping.class)) {
                    methodPath = method.getAnnotation(RequestMapping.class).value();
                    httpMethod = method.getAnnotation(RequestMapping.class).method()[0];
                }


                // Validate: don't allow same role in roles & excludedRoles
                if (access != null) {
                    Set<String> roles = new HashSet<>(Arrays.asList(access.roles()));
                    Set<String> excludedRoles = new HashSet<>(Arrays.asList(access.excludedRoles()));

                    Set<String> intersection = new HashSet<>(roles);
                    intersection.retainAll(excludedRoles);

                    if (!intersection.isEmpty()) {
                        throw new IllegalStateException(
                                "Conflict: Role(s) appear in both 'roles' and 'excludedRoles' in method: " + method.getName() +
                                        " → " + intersection
                        );
                    }
                }

                ClashScannedEndpointMetadata meta = new ClashScannedEndpointMetadata();
                meta.setEndpoints(methodPath);
                meta.setBasePath(basePath);
                meta.setHttpMethod(httpMethod.name());
                meta.setPublic(isPublic);
                meta.setController(clazz.getSimpleName());
                meta.setFullControllerName(clazz.getName());
                meta.setMethod(method.getName());
                meta.setContextPath(prefixPath);

                if (!isPublic) {
                    meta.setRoles(Set.of(access.roles()));
                    meta.setPermissions(Set.of(access.permissions()));
                }

                securedEndpoints.add(meta);
            }
        }
        endPoints.setEndpoints(securedEndpoints);
    }


}
