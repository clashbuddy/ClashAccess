package studio.clashbuddy.clashaccess.otp;



public class VerificationSessionDTO {
    private String userId;
    private String cbPayId;
    private String reason;

    public VerificationSessionDTO() {
    }

    public VerificationSessionDTO(String userId, String cbPayId, String reason) {
        this.userId = userId;
        this.cbPayId = cbPayId;
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCbPayId() {
        return cbPayId;
    }

    public void setCbPayId(String cbPayId) {
        this.cbPayId = cbPayId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}