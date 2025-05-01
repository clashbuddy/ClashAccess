package studio.clashbuddy.clashaccess.helpers;

import java.util.List;

public class MetadataPayload {
    private String id;
    private List<EndpointMeta> endpoints;

    public MetadataPayload() {
    }

    public MetadataPayload(String id, List<EndpointMeta> endpoints) {
        this.id = id;
        this.endpoints = endpoints;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EndpointMeta> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointMeta> endpoints) {
        this.endpoints = endpoints;
    }
}

