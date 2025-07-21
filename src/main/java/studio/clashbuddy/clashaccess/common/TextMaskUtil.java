package studio.clashbuddy.clashaccess.common;

public class TextMaskUtil {
    public static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***@***";

        String username = email.substring(0, at);
        String domain = email.substring(at);
        return username.charAt(0) + "****" + username.charAt(username.length() - 1) + domain;
    }
}
