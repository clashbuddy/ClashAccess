package studio.clashbuddy.clashaccess.auth.authentication;

public class ClashToken {
    private final String accessToken;
    private final String refreshToken;
    private final String type;

    public ClashToken(String accessToken, String refreshToken, String type) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.type = type;
    }

    public String getAccessToken() {
        return accessToken;
    }



    public String getRefreshToken() {
        return refreshToken;
    }


    public String getType() {
        return type;
    }
}
