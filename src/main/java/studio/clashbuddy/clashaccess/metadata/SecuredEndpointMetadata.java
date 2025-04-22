package studio.clashbuddy.clashaccess.metadata;

import java.util.Collections;
import java.util.Set;

public class SecuredEndpointMetadata {
    private String httpMethod;
    private String fullPath;
    private boolean isPublic;
    private String controller;
    private String fullControllerName;
    private String method;
    private Set<String> roles;
    private Set<String> permissions;


    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Set<String> getRoles() {
        return roles == null ? Collections.emptySet() : roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions == null ? Collections.emptySet() : permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getFullControllerName() {
        return fullControllerName;
    }

    public void setFullControllerName(String fullControllerName) {
        this.fullControllerName = fullControllerName;
    }
}
