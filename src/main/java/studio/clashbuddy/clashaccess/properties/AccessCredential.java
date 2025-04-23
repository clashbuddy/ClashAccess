package studio.clashbuddy.clashaccess.properties;

public class AccessCredential {

    private String endpoint;
    private String key;


    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getKey() {
        return key;
    }
}