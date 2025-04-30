package studio.clashbuddy.clashaccess.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ServiceType;
import studio.clashbuddy.clashaccess.security.AccessMetadataService;
import studio.clashbuddy.clashaccess.security.AccessRulesCompiler;
import studio.clashbuddy.clashaccess.security.RequireAccess;
import studio.clashbuddy.clashaccess.security.config.AccessRules;
import studio.clashbuddy.clashaccess.security.config.ProtectedRule;
import studio.clashbuddy.clashaccess.security.config.PublicRule;
import studio.clashbuddy.clashaccess.security.config.Rule;

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
    @Autowired
    private ApplicationEventPublisher publisher;

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

        if (!clashBuddyClashAccessProperties.getServiceType().equals(ServiceType.APPLICATION)) {
            publisher.publishEvent(new MetadataRefreshEvent(new HashSet<>()));
            return;
        }
        String scanStatus = securityProperties.isScan()
                ? "📦 Ready to process metadata endpoints."
                : "⚠️  Endpoint scanning is disabled. No endpoints will be processed.";

        logger.info(
                "\n" +
                        "✅ ClashAccess {} Initialization Summary:\n" +
                        "   🔧 Mode:              APPLICATION\n" +
                        "   🔗 Endpoint :         {}\n" +
                        "   🔍 Endpoint Scan:     {}\n" +
                        "   ✅ Module Enabled:     {}\n" +
                        "   {}\n",VersionReader.getVersion(),
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
                    var methodData = method.getAnnotation(GetMapping.class);
                    methodPath = getPaths(methodData.value(),methodData.path());
                    httpMethod = new RequestMethod[]{RequestMethod.GET};
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    var methodData = method.getAnnotation(PostMapping.class);
                    methodPath = getPaths(methodData.value(),methodData.path());
                    httpMethod = new RequestMethod[]{RequestMethod.POST};
                } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                    var methodData = method.getAnnotation(DeleteMapping.class);
                    methodPath = getPaths(methodData.value(),methodData.path());
                    httpMethod = new RequestMethod[]{RequestMethod.POST};
                } else if (method.isAnnotationPresent(PutMapping.class)) {
                    var methodData = method.getAnnotation(PutMapping.class);
                    methodPath = getPaths(methodData.value(),methodData.path());
                    httpMethod = new RequestMethod[]{RequestMethod.PUT};
                } else if (method.isAnnotationPresent(PatchMapping.class)) {
                    var methodData = method.getAnnotation(PatchMapping.class);
                    methodPath = getPaths(methodData.value(),methodData.path());
                    httpMethod = new RequestMethod[]{RequestMethod.PATCH};
                } else if (method.isAnnotationPresent(RequestMapping.class)) {
                    var methodData = method.getAnnotation(RequestMapping.class);
                    methodPath = getPaths(methodData.value(),methodData.path());
                    httpMethod = methodData.method().length==0? RequestMethod.values():methodData.method();
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
                meta.setIsPrivate(!isPublic, true);
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
        if (accessRules != null)
            validateNoConflicts(accessRules.getProtectedRules(), accessRules.getPublicRules());

        if (!securityProperties.isScan()) return;
        if (accessRules != null)
            if (accessRules.isAuthorizeAnyRequest())
                authorizeEny(securedEndpoints);

        endPoints.setEndpoints(securedEndpoints);
        endPoints.changeEndPointsToPrivate(accessRules);
        if (accessRules != null)
            unAuthorizedEnyPublic(accessRules.getPublicRules(), securedEndpoints);
        publisher.publishEvent(new MetadataRefreshEvent(endPoints.getMetaEndpoints()));
    }

    private String[] getPaths(String[] values, String[] paths){
        if(values == null || values.length ==0){
            if(paths == null  || paths.length==0)
                return new String[]{"/"};
            return paths;
        }
        return values;
    }

    private void unAuthorizedEnyPublic(Set<PublicRule> publicRules, Set<ClashScannedEndpointMetadata> scannedMetadataEndpoints) {
        if (publicRules.isEmpty()) return;
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (ClashScannedEndpointMetadata metadata : scannedMetadataEndpoints) {
            if (metadata.isPrivate) continue;
            for (PublicRule rule : publicRules) {
                String[] publicEndPoints = findMatchedPaths(antPathMatcher, metadata.getPublicEndpoints(), rule.getPaths().toArray(String[]::new));
                String[] publicMethods = findMatchedMethods(metadata.getHttpMethods(), rule.getMethods());
                metadata.changePrivateEndpointsAndMethods(publicEndPoints, publicMethods);
            }
        }
    }

    private void authorizeEny(Set<ClashScannedEndpointMetadata> scannedMetadataEndpoints) {
        for (ClashScannedEndpointMetadata clashScannedEndpointMetadata : scannedMetadataEndpoints)
            if (!clashScannedEndpointMetadata.isPrivate)
                clashScannedEndpointMetadata.setIsPrivate(true, false);
    }

    public static String[] findMatchedPaths(AntPathMatcher matcher, String[] pathsA, String[] pathsB) {
        List<String> matched = new ArrayList<>();

        if (pathsA == null || pathsB == null) {
            return new String[0];
        }

        for (String a : pathsA) {
            for (String b : pathsB) {
                if (matcher.match(a, b) || matcher.match(b, a)) {
                    matched.add(b); // or matched.add(a) depending on what you want
                    break; // break inner loop to avoid duplicates
                }
            }
        }

        return matched.toArray(new String[0]);
    }

    public static String[] findMatchedMethods(String[] methodsA, String[] methodsB) {
        List<String> matched = new ArrayList<>();

        if (methodsA == null || methodsB == null) {
            return new String[0];
        }
        for (String a : methodsA) {
            for (String b : methodsB) {
                if (a.equalsIgnoreCase(b)) {
                    matched.add(b); // or matched.add(a) depending on what you want
                    break; // break inner loop to avoid duplicates
                }
            }
        }
        return matched.toArray(new String[0]);
    }

    private void validateNoConflicts(Set<ProtectedRule> rules, Set<PublicRule> publicRules) {
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

    private Set<String> buildPairs(Set<? extends Rule> rules) {
        Set<String> pairs = new HashSet<>();
        for (Rule rule : rules) {
            for (String path : rule.getPaths()) {
                if (rule.getMethods().length == 0) {
                    pairs.add(path + "#ALL");
                } else {
                    for (String method : rule.getMethods()) {
                        pairs.add(path + "#" + method);
                    }
                }
            }
        }
        return pairs;
    }


}
