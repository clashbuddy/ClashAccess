package studio.clashbuddy.clashaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

import java.util.*;

class AccessValidator {

    public static AuthorizedUser validateOneRoleAndPermissions(HttpServletRequest request, String[] expectedRoles, String[] excludedRoles, String[] expectedPermissions, String[] excludedPermissions, I18nHelper i18nHelper) {
        var authorizedUser = validateAndGetAuthorizedUser(request,i18nHelper);
        var userRoles = authorizedUser.getRoles();
        var userPermissions = authorizedUser.getPermissions();
        validateRolesOrPermissions(expectedRoles, userRoles, "role", true,i18nHelper);
        validateRolesOrPermissions(expectedPermissions, userPermissions, "permission", true,i18nHelper);
        validateRolesOrPermissions(excludedRoles, userRoles, "role", false,i18nHelper);
        validateRolesOrPermissions(excludedPermissions, userPermissions, "permission", false,i18nHelper);
        return authorizedUser;
    }

    private static void validateRolesOrPermissions(String[] expected, Set<String> available, String message, boolean isExpected, I18nHelper helper) {
        if (expected.length == 0) return;
        boolean isContained = Arrays.stream(expected)
                .anyMatch(req -> available.stream().anyMatch(a -> a.equalsIgnoreCase(req)));

        if (isExpected && !isContained)
            throw new ClashAccessDeniedException(helper.i18n("{clashaccess.error.not-contained-denied}",message), 403);
        else if (!isExpected && isContained)
            throw new ClashAccessDeniedException(helper.i18n("{clashaccess.error.contained-denied}",message), 403);
    }

    private static AuthorizedUser validateAndGetAuthorizedUser(HttpServletRequest request,I18nHelper i18nHelper) {
        validateHeader(request,i18nHelper);
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

    private static void validateHeader(HttpServletRequest request,I18nHelper helper) {
        if (!StringUtils.hasText(request.getHeader("x-ca-uid")))
            throw new ClashAccessDeniedException(helper.i18n("{clashaccess.error.missing.user-id-header}"), 403);

        if (!StringUtils.hasText(request.getHeader("x-ca-urs")))
            throw new ClashAccessDeniedException(helper.i18n("{clashaccess.error.missing.roles-header}"), 403);
    }
}