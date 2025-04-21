package studio.clashbuddy.clashaccess.exceptions;

public class ClashAccessDeniedException extends RuntimeException {
    private final int status;

    public ClashAccessDeniedException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
