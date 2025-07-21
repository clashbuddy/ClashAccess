package studio.clashbuddy.clashaccess.otp;


public class OtpVerificationDTO {
    private String userId;
    private String cbPayId;
    private String reason;
    private String otp;
    private String method;

    public OtpVerificationDTO() {
    }

    public OtpVerificationDTO(String userId, String cbPayId, String reason, String otp, String method) {
        this.userId = userId;
        this.cbPayId = cbPayId;
        this.reason = reason;
        this.otp = otp;
        this.method = method;
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

    public String  getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}