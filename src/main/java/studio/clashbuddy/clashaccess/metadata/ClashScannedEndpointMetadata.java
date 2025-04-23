package studio.clashbuddy.clashaccess.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class ClashScannedEndpointMetadata {
    private String httpMethod;
    private String[] endpoints;
    private String basePath;
    private String contextPath;
    private boolean isPublic;
    private String controller;
    private String fullControllerName;
    private String method;
    private Set<String> roles;
    private Set<String> permissions;

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getMainEndpoint() {
        if (basePath == null) basePath = "";
        if (endpoints == null || endpoints.length == 0 || endpoints[0] == null)
            return removeDuplicateSlashes(contextPath+"/"+basePath.replaceAll("^/+", "").replaceAll("/+$", "") + "/");

        String cleanBase = basePath.replaceAll("^/+", "").replaceAll("/+$", "");
        String cleanEndpoint = endpoints[0].replaceAll("^/+", "").replaceAll("/+$", "");

         return  removeDuplicateSlashes(contextPath+"/"+cleanBase + "/" + cleanEndpoint);
    }

    private String removeDuplicateSlashes(String path) {
        if (path == null) return null;
        // Replace multiple slashes with a single slash
        String cleaned = path.replaceAll("/{2,}", "/");

        // Optional: if you want to remove trailing slashes (but keep leading)
        if (cleaned.length() > 1 && cleaned.endsWith("/")) {
            cleaned = cleaned.replaceAll("/+$", "");
        }

        return cleaned;
    }


    public String[] getEndpoints() {
        return endpoints;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setEndpoints(String[] endpoints) {
        this.endpoints = endpoints;
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

    @Override
    public String toString() {
        return "SecuredEndpointMetadata{" +
                "httpMethod='" + httpMethod + '\'' +
                ", endpoints=" + Arrays.toString(endpoints) +
                ", basePath='" + basePath + '\'' +
                ", contextPath='" + contextPath + '\'' +
                ", isPublic=" + isPublic +
                ", controller='" + controller + '\'' +
                ", fullControllerName='" + fullControllerName + '\'' +
                ", method='" + method + '\'' +
                ", roles=" + roles +
                ", mainEndpoint='" + getMainEndpoint() + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
