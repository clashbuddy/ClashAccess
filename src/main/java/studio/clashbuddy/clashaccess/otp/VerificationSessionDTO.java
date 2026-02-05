package studio.clashbuddy.clashaccess.otp;



public class VerificationSessionDTO {
    private String userId;
    private String cbPayId;
    private String reason;
    private Object metadata;

    public VerificationSessionDTO() {
    }

    public VerificationSessionDTO(String userId, String cbPayId, String reason, Object metadata) {
        this.userId = userId;
        this.cbPayId = cbPayId;
        this.reason = reason;
        this.metadata = metadata;
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

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
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