package studio.clashbuddy.clashaccess.security;

import java.util.Set;

public class AuthorizedUser {

    private String userId;
    private String userId2;
    private Set<String> roles;
    private Set<String> permissions;


    public AuthorizedUser(String userId,String userId2, Set<String> roles, Set<String> permissions) {
        this.userId = userId;
        this.roles = roles;
        this.permissions = permissions;
        this.userId2 = userId2;

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

    public String getUserId2() {
        return userId2;
    }
}
