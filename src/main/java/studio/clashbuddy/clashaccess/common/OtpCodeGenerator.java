package studio.clashbuddy.clashaccess.common;

import java.security.SecureRandom;

public class OtpCodeGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    private OtpCodeGenerator() {
        // prevent instantiation
    }

    public static String generateNumericCode(int length) {
        if (length <= 0 || length > 10) {
            throw new IllegalArgumentException("OTP length must be between 1 and 10");
        }

        int max = (int) Math.pow(10, length);
        int min = (int) Math.pow(10, length - 1);

        int code = secureRandom.nextInt(max - min) + min;
        return String.valueOf(code);
    }

    // Default 6-digit OTP
    public static String generate6DigitCode() {
        return generateNumericCode(6);
    }

    public static String generateRandomTextCode(int length) {
        String ALPHANUMERIC = "AB@qrs789%NtuCDEFijklmno56O@PQpvwxyGHI#JKLMV$WXYZabcdefghz01234RSTU";

        if (length <= 0 || length > 100) {
            throw new IllegalArgumentException("Text code length must be between 1 and 100");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
}
