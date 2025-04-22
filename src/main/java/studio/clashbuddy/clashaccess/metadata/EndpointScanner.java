package studio.clashbuddy.clashaccess.metadata;

import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import studio.clashbuddy.clashaccess.properies.ClashBuddySecurityProperties;
import studio.clashbuddy.clashaccess.security.RequireAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Component
public class EndpointScanner implements ApplicationListener<ApplicationReadyEvent> {

    private final AvailableEndPoints endPoints;
    private final ClashBuddySecurityProperties securityProperties;

    public EndpointScanner(AvailableEndPoints endPoints, ClashBuddySecurityProperties securityProperties) {
        this.endPoints = endPoints;
        this.securityProperties = securityProperties;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!securityProperties.isScan()) return;
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> controllers = context.getBeansWithAnnotation(RestController.class);
        Set<SecuredEndpointMetadata> securedEndpoints = new HashSet<>();
        for (Object bean : controllers.values()) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            String basePath = Optional.ofNullable(clazz.getAnnotation(RequestMapping.class))
                    .map(a -> a.value().length > 0 ? a.value()[0] : "")
                    .orElse("");

            for (Method method : clazz.getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) continue;

                RequireAccess access = method.getAnnotation(RequireAccess.class);
                boolean isPublic = access == null;

                String methodPath = "";
                RequestMethod httpMethod = RequestMethod.GET;

                if (method.isAnnotationPresent(GetMapping.class)) {
                    methodPath = method.getAnnotation(GetMapping.class).value()[0];
                    httpMethod = RequestMethod.GET;
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    methodPath = method.getAnnotation(PostMapping.class).value()[0];
                    httpMethod = RequestMethod.POST;
                } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                    methodPath = method.getAnnotation(DeleteMapping.class).value()[0];
                    httpMethod = RequestMethod.DELETE;
                }
                else if (method.isAnnotationPresent(PutMapping.class)) {
                    methodPath = method.getAnnotation(PutMapping.class).value()[0];
                    httpMethod = RequestMethod.PUT;
                }
                else if (method.isAnnotationPresent(PatchMapping.class)) {
                    methodPath = method.getAnnotation(PatchMapping.class).value()[0];
                    httpMethod = RequestMethod.PATCH;
                } else if (method.isAnnotationPresent(RequestMapping.class)) {
                    methodPath = method.getAnnotation(RequestMapping.class).value()[0];
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

                SecuredEndpointMetadata meta = new SecuredEndpointMetadata();
                meta.setFullPath(basePath + methodPath);
                meta.setHttpMethod(httpMethod.name());
                meta.setPublic(isPublic);
                meta.setController(clazz.getSimpleName());
                meta.setFullControllerName(clazz.getName());
                meta.setMethod(method.getName());

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
