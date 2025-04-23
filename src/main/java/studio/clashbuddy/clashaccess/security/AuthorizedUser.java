package studio.clashbuddy.clashaccess.security;

import java.util.Set;

public class AuthorizedUser {

    private String userId;
    private Set<String> roles;
    private Set<String> permissions;


    public AuthorizedUser(String userId, Set<String> roles, Set<String> permissions) {
        this.userId = userId;
        this.roles = roles;
        this.permissions = permissions;

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }


}
