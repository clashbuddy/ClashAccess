package studio.clashbuddy.clashaccess.metadata;

import java.util.List;

public class OrganizedEndpoints {

    private final String controllerName;
    private String fullControllerName;
    private List<ClashScannedEndpointMetadata> metadata;


    public OrganizedEndpoints(String fullControllerName, List<ClashScannedEndpointMetadata> metadata) {
        var m = metadata.get(0);
        if (m == null)
            controllerName = "";
        else
            controllerName = m.getController();
        this.fullControllerName = fullControllerName;
        this.metadata = metadata;


    }

    public String getControllerName() {
        return controllerName;
    }


    public String getFullControllerName() {
        return fullControllerName;
    }

    public void setFullControllerName(String fullControllerName) {
        this.fullControllerName = fullControllerName;
    }

    public List<ClashScannedEndpointMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<ClashScannedEndpointMetadata> metadata) {
        this.metadata = metadata;
    }

}
