package studio.clashbuddy.clashaccess.exceptions;

public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);

    }

    public int getStatus() {
        return 429;
    }
}
