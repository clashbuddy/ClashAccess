package studio.clashbuddy.clashaccess.security;

import java.util.List;
import java.util.Map;

public class AuthorizedUser {

    private  String userId;
    private  List<String> roles;
    private  List<String> permissions;
    private  Map<String, String> extraSecurityAttributes;


    public AuthorizedUser(String userId, List<String> roles, List<String> permissions, Map<String, String> extraSecurityAttributes) {
        this.userId = userId;
        this.roles = roles;
        this.permissions = permissions;
        this.extraSecurityAttributes = extraSecurityAttributes;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setExtraSecurityAttributes(Map<String, String> extraSecurityAttributes) {
        this.extraSecurityAttributes = extraSecurityAttributes;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public Map<String, String> getExtraSecurityAttributes() {
        return extraSecurityAttributes;
    }
}
