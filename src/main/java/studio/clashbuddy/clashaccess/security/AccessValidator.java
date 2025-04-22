package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;

import java.util.*;

public class AccessValidator {

    public static AuthorizedUser validateOneRoleAndPermissions(HttpServletRequest request, String[] expectedRoles, String[] excludedRoles, String[] expectedPermissions, String[] excludedPermissions, String[] extraSecurityAttributes) {
        var authorizedUser = validateAndGetAuthorizedUser(request, extraSecurityAttributes);
        var userRoles = authorizedUser.getRoles();
        var userPermissions = authorizedUser.getPermissions();
        validateRolesOrPermissions(expectedRoles, userRoles, "role", true);
        validateRolesOrPermissions(expectedPermissions, userPermissions, "permission", true);
        validateRolesOrPermissions(excludedRoles, userRoles, "role", false);
        validateRolesOrPermissions(excludedPermissions, userPermissions, "permission", false);
        return authorizedUser;
    }

    private static void validateRolesOrPermissions(String[] expected, Set<String> available, String message, boolean isExpected) {
        if (expected.length == 0) return;
        boolean isContained = Arrays.stream(expected)
                .anyMatch(req -> available.stream().anyMatch(a -> a.equalsIgnoreCase(req)));

        if (isExpected && !isContained)
            throw new ClashAccessDeniedException("Access Denied: Required " + message + " not found", 403);
        else if (!isExpected && isContained)
            throw new ClashAccessDeniedException("Access Denied: " + message + " is explicitly excluded", 403);
    }

    private static AuthorizedUser validateAndGetAuthorizedUser(HttpServletRequest request, String[] extraSecurityAttributes) {
        validateHeader(request);
        var headerUserId = Objects.requireNonNull(request.getHeader("x-ca-uid"));
        Set<String> headerUserPermissions = new HashSet<>();
        Set<String> headerUserRoles = new HashSet<>();
        fillFillableList(request, headerUserPermissions, "x-ca-ups");
        fillFillableList(request, headerUserRoles, "x-ca-urs");

        return new AuthorizedUser(headerUserId, headerUserRoles, headerUserPermissions);

    }

    private static void fillFillableList(HttpServletRequest request, Set<String> fillable, String headerKey) {
        if (!StringUtils.hasText(request.getHeader(headerKey))) return;
        request.getHeaders(headerKey).asIterator().forEachRemaining(data -> {
            try {
                fillable.add(data);
            } catch (RuntimeException ignored) {
            }
        });
    }

    private static void validateHeader(HttpServletRequest request) {
        if (!StringUtils.hasText(request.getHeader("x-ca-uid")))
            throw new ClashAccessDeniedException("Missing middle layer user id header", 403);
        if (!StringUtils.hasText(request.getHeader("x-ca-ups")))
            throw new ClashAccessDeniedException("Missing middle layer user permission header", 403);
        if (!StringUtils.hasText(request.getHeader("x-ca-urs")))
            throw new ClashAccessDeniedException("Missing middle layer user roles header", 403);
    }
}