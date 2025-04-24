package studio.clashbuddy.clashaccess.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import studio.clashbuddy.clashaccess.security.AccessMetadataService;
import studio.clashbuddy.clashaccess.security.AccessRulesCompiler;
import studio.clashbuddy.clashaccess.security.RequireAccess;
import studio.clashbuddy.clashaccess.security.config.AccessRule;
import studio.clashbuddy.clashaccess.security.config.AccessRules;

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

    @Autowired(required = false)
    private AccessRules accessRules;
    @Autowired
    private AccessMetadataService accessMetadataService;

    public EndpointScanner(ScannedMetadataEndpoints endPoints, ClashBuddySecurityClashAccessAppProperties clashBuddySecurityClashAccessAppProperties, ClashBuddyClashAccessProperties clashBuddyClashAccessProperties) {
        this.endPoints = endPoints;
        this.securityProperties = clashBuddySecurityClashAccessAppProperties;
        this.clashBuddyClashAccessProperties = clashBuddyClashAccessProperties;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        if (accessRules != null) {
            var compiled = AccessRulesCompiler.compile(accessRules);
            var compilePublic = AccessRulesCompiler.compilePublic(accessRules);

            accessMetadataService.setCompiledRules(compiled);
            accessMetadataService.setCompiledPublicRules(compilePublic);
        }

        if (!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) return;
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
                RequestMethod[] httpMethod = null;

                if (method.isAnnotationPresent(GetMapping.class)) {
                    methodPath = method.getAnnotation(GetMapping.class).value();
                    httpMethod = new RequestMethod[]{RequestMethod.GET};
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    methodPath = method.getAnnotation(PostMapping.class).value();
                    httpMethod = new RequestMethod[]{RequestMethod.POST};
                } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                    methodPath = method.getAnnotation(DeleteMapping.class).value();
                    httpMethod = new RequestMethod[]{RequestMethod.DELETE};
                } else if (method.isAnnotationPresent(PutMapping.class)) {
                    methodPath = method.getAnnotation(PutMapping.class).value();
                    httpMethod = new RequestMethod[]{RequestMethod.PUT};
                } else if (method.isAnnotationPresent(PatchMapping.class)) {
                    methodPath = method.getAnnotation(PatchMapping.class).value();
                    httpMethod = new RequestMethod[]{RequestMethod.PATCH};
                } else if (method.isAnnotationPresent(RequestMapping.class)) {
                    methodPath = method.getAnnotation(RequestMapping.class).value();
                    httpMethod = method.getAnnotation(RequestMapping.class).method();
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
                meta.setHttpMethods(httpMethod);
                meta.setIsPublic(isPublic);
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
        validateNoConflicts(accessRules.getRules(),accessRules.getPublicRules());
        if (!securityProperties.isScan()) return;
        endPoints.setEndpoints(securedEndpoints);
        endPoints.changeEndPointsToPrivate(accessRules);
    }


    private void validateNoConflicts(Set<AccessRule> rules, Set<AccessRule> publicRules) {
        Set<String> protectedPairs = buildPairs(rules);
        Set<String> publicPairs = buildPairs(publicRules);

        for (String publicEntry : publicPairs) {
            if (protectedPairs.contains(publicEntry)) {
                throw new IllegalStateException(
                        "\n\n🔥🔥 ClashAccess Configuration Error 🔥🔥\n" +
                                "🚫 Conflict detected between Protected and Public rules!\n" +
                                "➡️  Endpoint and Method: [" + publicEntry.replace("#", " ") + "]\n" +
                                "❌ You cannot declare the same endpoint as both protected and unprotected!\n" +
                                "🛠️  Please fix your AccessRules configuration.\n"
                );
            }
        }
    }

    private Set<String> buildPairs(Set<AccessRule> rules) {
        Set<String> pairs = new HashSet<>();
        for (AccessRule rule : rules) {
            for (String path : rule.getPaths()) {
                if (rule.getMethods().isEmpty()) {
                    pairs.add(path + "#ALL");
                } else {
                    for (RequestMethod method : rule.getMethods()) {
                        pairs.add(path + "#" + method.name());
                    }
                }
            }
        }
        return pairs;
    }


}
