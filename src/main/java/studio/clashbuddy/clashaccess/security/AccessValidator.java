package studio.clashbuddy.clashaccess.security;

import com.spondias.fintech.common.exections.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.*;

public class AccessValidator {
    public static Principal validateOneRoleAndPermissions(HttpServletRequest request, @NonNull List<UserRole> userRole, @NonNull UserPermission ... permissions){
        var principal = validateAndGetPrincipal(request);
        validateRoles(userRole, principal);
        List<UserPermission> allowedPermissions = Arrays.asList(permissions);
        if(allowedPermissions.isEmpty()) return principal;
        var userPermissions =principal.getPermissions();
        if(allowedPermissions.stream().noneMatch(userPermissions::contains))
            throw new ApiException("You don't have permission to access this service", HttpStatus.FORBIDDEN);
        return principal;
    }

    private static void validateRoles(List<UserRole> userRoles, Principal principal) {
        if(userRoles.contains(UserRole.ALL_ROLES)) return;
        if(!userRoles.contains(principal.getRole()))
            throw new ApiException("Your role is not allowed to access this service", HttpStatus.FORBIDDEN);
    }

    private static Principal validateAndGetPrincipal(HttpServletRequest request){
        validateHeader(request);
        var headerUserRole = UserRole.valueOf(Objects.requireNonNull(request.getHeader("x-auth-user-role")));
        var headerUserId = Objects.requireNonNull(request.getHeader("x-auth-user-id"));
        var clientAppType = ClientAppType.valueOf(Objects.requireNonNull(request.getHeader("x-auth-client-app-type")).toUpperCase());
        List<UserPermission> headerUserPermissions = new LinkedList<>();
        setUserPermissions(request, headerUserPermissions);
        var credentialId =  Objects.requireNonNull(request.getHeader("x-auth-credential-id"));
        var deviceId = Objects.requireNonNull(request.getHeader("x-auth-device-id"));
        return new Principal(headerUserId,credentialId,deviceId,headerUserPermissions,headerUserRole,clientAppType);
    }

    private static void setUserPermissions(HttpServletRequest request, List<UserPermission> headerUserPermissions) {
        if(StringUtils.isEmpty(request.getHeader("x-auth-user-permissions"))) return;
        request.getHeaders("x-auth-user-permissions").asIterator().forEachRemaining(permission-> {
            try {
               var userPermission= UserPermission.valueOf(permission);
                headerUserPermissions.add(userPermission);
            }catch (RuntimeException ignored){
            }
        });
    }


    private static void validateHeader(HttpServletRequest request) {
         if(StringUtils.isEmpty(request.getHeader("x-auth-user-id")))
             throw new ApiException("Missing middle layer user id header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader("x-auth-user-role")))
            throw new ApiException("Missing middle layer user role header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader("x-auth-credential-id")))
            throw new ApiException("Missing middle layer credential id header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader("x-auth-device-id")))
            throw new ApiException("Missing middle layer device id header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader( "x-auth-client-app-type")))
            throw new ApiException("Missing client app type header", HttpStatus.FORBIDDEN);
    }
}
