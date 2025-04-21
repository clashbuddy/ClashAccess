package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;

import java.util.*;

public class AccessValidator {

    public static AuthorizedUser validateOneRoleAndPermissions(HttpServletRequest request, String[] expectedRoles, String[] excludedRoles,String [] expectedPermissions, String[] excludedPermissions, String[] extraSecurityAttributes) {
        var authorizedUser = validateAndGetAuthorizedUser(request,extraSecurityAttributes);

        validateRolesOrPermissions(expectedRoles,authorizedUser.getRoles(),"You don't have role to access this service", 401,true);
        validateRolesOrPermissions(expectedPermissions,authorizedUser.getPermissions(),"You don't have permission to access this service", 401,true);

        validateRolesOrPermissions(excludedRoles,authorizedUser.getRoles(),"You have role which is not allowed accessed to use this service", 401,false);
        validateRolesOrPermissions(excludedPermissions,authorizedUser.getPermissions(),"You have permission which is allowed not accessed to use this service", 401,false);
        return authorizedUser;
    }

    private static void validateRolesOrPermissions(String[] expected, List<String> available,String message, int code, boolean isExpected) {
        if(expected.length == 0) return;
        boolean isContained = false;
        for (String av : available)
            if (available.stream().anyMatch(a->a.equalsIgnoreCase(av))){
                isContained = true;
                break;
            }
        if(isExpected) {
            if (!isContained)
                throw new ClashAccessDeniedException(message, code);
        }else{
            if (isContained)
                throw new ClashAccessDeniedException(message, code);
        }
    }


    private static AuthorizedUser validateAndGetAuthorizedUser(HttpServletRequest request,String [] extraSecurityAttributes) {
        validateHeader(request);
        var headerUserId = Objects.requireNonNull(request.getHeader("x-ca-uid"));
        List<String> headerUserPermissions = new LinkedList<>();
        List<String> headerUserRoles = new LinkedList<>();
        fillFillableList(request, headerUserPermissions,"x-ca-ups");
        fillFillableList(request, headerUserRoles,"x-ca-urs");
        Map<String, String> extraSecurityAtt = new HashMap<>();
        for (String extraSecurityAttribute : extraSecurityAttributes) {
            String value = request.getHeader(extraSecurityAttribute);
            if (!StringUtils.hasText(value))
                extraSecurityAtt.put(extraSecurityAttribute, value);
        }
        return new AuthorizedUser(headerUserId, headerUserRoles,headerUserPermissions,extraSecurityAtt);

    }

    private static void fillFillableList(HttpServletRequest request, List<String> fillable,String headerKey) {
        if(!StringUtils.hasText(request.getHeader(headerKey))) return;
        request.getHeaders(headerKey).asIterator().forEachRemaining(data-> {
            try {
                fillable.add(data);
            }catch (RuntimeException ignored){
            }
        });
    }

    private static void validateHeader(HttpServletRequest request) {
        if(!StringUtils.hasText(request.getHeader("x-ca-uid")))
            throw new ClashAccessDeniedException("Missing middle layer user id header", 403);
        if(!StringUtils.hasText(request.getHeader("x-ca-ups")))
            throw new ClashAccessDeniedException("Missing middle layer user permission header",403);
        if(!StringUtils.hasText(request.getHeader("x-ca-urs")))
            throw new ClashAccessDeniedException("Missing middle layer user roles header", 403);
    }
}