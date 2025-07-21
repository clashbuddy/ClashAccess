package studio.clashbuddy.clashaccess.common;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.utils.I18nHelper;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;

public class TOTPUtil {
    private static final Logger log = LoggerFactory.getLogger(TOTPUtil.class);

    public static String generateSecret() {
        byte[] buffer = new byte[10];
        new SecureRandom().nextBytes(buffer);
        return new Base32().encodeToString(buffer).replace("=", "");
    }

    public static String buildTotpUri(String secret, String userClashBuddyPayId, String issuer) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, userClashBuddyPayId, secret, issuer
        );
    }


    public static void verifyCode(String base32Secret, String code, I18nHelper i18nHelper) {
        try {
            byte[] keyBytes = new Base32().decode(base32Secret);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
            Instant now = Instant.now();

            for (int i = -1; i <= 1; i++) {
                Instant time = now.plusSeconds(i * totp.getTimeStep().getSeconds());
                int otp = totp.generateOneTimePassword(secretKey, time);
                if (String.format("%06d", otp).equals(code)) {
                    return;
                }
            }

            throw new ClashAccessDeniedException(i18nHelper.i18n("{auth.error.invalid-authenticator-code}"), 401);
        } catch (ClashAccessDeniedException clashBuddyIdentityException) {
            throw clashBuddyIdentityException;
        } catch (Exception e) {
            log.error("Authenticator validation failed: {}", e.getMessage());
            throw new ClashAccessDeniedException(i18nHelper.i18n("{auth.error.invalid-authenticator-code}"), 401);
        }
    }
}
