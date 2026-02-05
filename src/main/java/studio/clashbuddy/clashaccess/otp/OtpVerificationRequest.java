package studio.clashbuddy.clashaccess.otp;


import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class OtpVerificationRequest {
    private String verificationId;
    private String otp;
    private String method;
    private String reason;
    private Locale locale;
    public OtpVerificationRequest() {
    }

    public OtpVerificationRequest(String verificationId, String otp, String method, String reason) {
        this.verificationId = verificationId;
        this.otp = otp;
        this.method = method;
        this.reason = reason;
        this.locale = LocaleContextHolder.getLocale();
    }

    public OtpVerificationRequest(String verificationId, String otp, String method, String reason,Locale locale) {
        this.verificationId = verificationId;
        this.otp = otp;
        this.method = method;
        this.reason = reason;
        this.locale = locale;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}