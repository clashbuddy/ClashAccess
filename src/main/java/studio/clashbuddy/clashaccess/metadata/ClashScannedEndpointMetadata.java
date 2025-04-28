package studio.clashbuddy.clashaccess.metadata;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

public class ClashScannedEndpointMetadata {
    private String[] httpMethods;
    private String[] endpoints;
    private String basePath;
    private String contextPath;
    private String controller;
    private String fullControllerName;
    private String method;
    private Set<String> roles;
    private Set<String> permissions;
    private String[] publicHttpMethods;
    private String[] publicEndpoints;
    boolean isPrivate = false;
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public String[] getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(RequestMethod[] httpMethods) {
      this.httpMethods = Arrays.stream(httpMethods).map(RequestMethod::name).toArray(String[]::new);
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

    void setIsPrivate(boolean isPrivate, boolean isAnnotated) {
        if(isPrivate){
            publicHttpMethods = httpMethods;
            publicEndpoints = endpoints;
        }
        else {
            publicHttpMethods = new String[0];
            publicEndpoints = new String[0];
        }
        if(isAnnotated)
            this.isPrivate= isPrivate;
    }

    void changePublicEndpoints(String[] privateEndpoints) {
        publicEndpoints = removePrivates(publicEndpoints, privateEndpoints);
    }

    void changePublicMethods(String[] privateMethods) {
        publicHttpMethods = removePrivates(publicHttpMethods, privateMethods);
    }

    void changePrivateEndpointsAndMethods(String[] publicEndpoints, String[] publicMethods){
        this.publicEndpoints = publicEndpoints;
        this.publicHttpMethods = publicMethods;
    }

    public String[] getPublicHttpMethods() {
        return publicHttpMethods;
    }

    public String[] getPublicEndpoints() {
        return publicEndpoints;
    }

    private String[] removePrivates(String[] publics, String[] privates) {
        if (privates == null || privates.length == 0) {
            return publics;
        }

        Set<String> privateSet = new HashSet<>(Arrays.asList(privates));
        List<String> filtered = new ArrayList<>();

        for (String item : publics) {
            if (!privateSet.contains(item)) {
                filtered.add(item);
            }
        }

        return filtered.toArray(new String[0]);
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
                "httpMethod='" + Arrays.toString(httpMethods) + '\'' +
                ", endpoints=" + Arrays.toString(endpoints) +
                ", basePath='" + basePath + '\'' +
                ", contextPath='" + contextPath + '\'' +
                ", publicHttpMethods=" + Arrays.toString(publicHttpMethods) +'\''+
                ", publicEndpoints=" + Arrays.toString(publicEndpoints) +'\''+
                ", controller='" + controller + '\'' +
                ", fullControllerName='" + fullControllerName + '\'' +
                ", method='" + method + '\'' +
                ", roles=" + roles +
                ", mainEndpoint='" + getMainEndpoint() + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
