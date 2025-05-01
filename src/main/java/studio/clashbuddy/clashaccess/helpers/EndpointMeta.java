package studio.clashbuddy.clashaccess.helpers;

import java.util.List;

public class EndpointMeta {
    private List<String> endpoints;
    private List<String> methods;

    public EndpointMeta() {
    }

    public EndpointMeta(List<String> endpoints, List<String> methods) {
        this.endpoints = endpoints;
        this.methods = methods;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
