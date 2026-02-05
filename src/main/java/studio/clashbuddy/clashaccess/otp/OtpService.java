package studio.clashbuddy.clashaccess.otp;


import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import studio.clashbuddy.clashaccess.common.OtpCodeGenerator;
import studio.clashbuddy.clashaccess.common.TOTPUtil;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.utils.I18nHelper;


import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final String OTP_PREFIX = "otp:";

    private final I18nHelper i18nHelper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final VerificationSessionService verificationSessionService;

    public OtpService(I18nHelper i18nHelper, RedisTemplate<String, Object> redisTemplate, VerificationSessionService verificationSessionService) {
        this.i18nHelper = i18nHelper;
        this.redisTemplate = redisTemplate;
        this.verificationSessionService = verificationSessionService;
    }

    public Pair<String, String> createOtp(
            String userId, String cbPayId, String reason,
            String method, int otpLength, long ttl, TimeUnit unit
    ){
        return createOtp(userId, cbPayId, reason, method, otpLength, ttl, unit,null);
    }

    public Pair<String, String> createOtp(
            String userId, String cbPayId, String reason,
            String method, int otpLength, long ttl, TimeUnit unit, Object metadata
    ) {

        String verificationId = UUID.randomUUID().toString();
        String otp = "";

        if ("AUTHENTICATOR".equals(method)) {
            verificationId = createVerificationSession(userId, cbPayId, reason, ttl, unit,metadata);
        } else {
            otp = OtpCodeGenerator.generateNumericCode(otpLength);
            OtpVerificationDTO dto = new OtpVerificationDTO(userId, cbPayId, reason, otp, method);
            storeOtp(verificationId, dto, ttl, unit);
        }

        return Pair.of(verificationId, otp);
    }


    public OtpVerificationDTO verifyOtpOnly(OtpVerificationRequest request) {
        var dto = validateOtpAndGet(request);
        deleteOtp(request.getVerificationId());
        return dto;
    }

    public Pair<String, OtpVerificationDTO> verifyOtpAndCreateSession(OtpVerificationRequest request) {
        var dto = validateOtpAndGet(request);
        deleteOtp(request.getVerificationId());
        var sessionId = createVerificationSession(dto.getUserId(), dto.getCbPayId(), dto.getReason(), 15, TimeUnit.MINUTES,null);
        return Pair.of(sessionId, dto);
    }

    public OtpVerificationDTO verifyAuthenticatorCode(OtpVerificationRequest request) {
        var dto = requireAuthenticatorOtp(request);
        verifyTOTPCode(dto.getUserId(), Optional.ofNullable(request.getOtp()), request.getLocale());
        deleteOtp(request.getVerificationId());
        return dto;
    }

    public String verifyAuthenticatorAndCreateSession(OtpVerificationRequest request) {
        var dto = requireAuthenticatorOtp(request);
        verifyTOTPCode(dto.getUserId(), Optional.ofNullable(request.getOtp()), request.getLocale());
        deleteOtp(request.getVerificationId());
        return createVerificationSession(dto.getUserId(), dto.getCbPayId(), dto.getReason(), 15, TimeUnit.MINUTES,null);
    }

    public VerificationSessionDTO verifySession(String sessionId, String reason) {
        return verifySession(sessionId, reason, LocaleContextHolder.getLocale());
    }

    public VerificationSessionDTO verifySession(String sessionId, String reason, Locale locale) {
        var session = verificationSessionService.getSession(sessionId);
        if (session == null)
            throw new ClashAccessDeniedException(i18nHelper.i18n("{otp.session.not-found}", locale), 404);
        if (!session.getReason().equals(reason))
            throw new ClashAccessDeniedException(i18nHelper.i18n("{otp.session.reason-mismatch}", locale, reason), 400);
        verificationSessionService.deleteSession(sessionId);
        return session;
    }

    // --- Internal Logic ---

    private OtpVerificationDTO validateOtpAndGet(OtpVerificationRequest request) {
        var dto = getOtp(request.getVerificationId());

        if (dto == null) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{verify.error.invalid-otp}", request.getLocale()), 404);
        }

        if (!dto.getMethod().equals(request.getMethod())) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{otp.verification.method-mismatch}", request.getLocale(), request.getMethod()), 403);
        }

        if (!dto.getOtp().equals(request.getOtp())) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{otp.verification.invalid-code}", request.getLocale(), request.getOtp()), 401);
        }

        if (!dto.getReason().equals(request.getReason())) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{otp.verification.invalid-code}", request.getLocale(), request.getOtp()), 401);
        }

        return dto;
    }

    private OtpVerificationDTO requireAuthenticatorOtp(OtpVerificationRequest request) {
        var dto = getOtp(request.getVerificationId());

        if (dto == null || !"AUTHENTICATOR".equals(dto.getMethod())) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{twofa.error.code-not-valid-expired}", request.getLocale(), request.getVerificationId()), 404);
        }
        if (!dto.getReason().equals(request.getReason())) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{twofa.error.code-not-valid-expired}", request.getLocale(), request.getVerificationId()), 404);
        }
        return dto;
    }

    private void verifyTOTPCode(String code, Optional<String> secretOpt, Locale locale) {
        if (secretOpt.isEmpty()) {
            throw new ClashAccessDeniedException(i18nHelper.i18n("{twofa.error.code-not-valid-expired}", locale), 401);
        }

        TOTPUtil.verifyCode(secretOpt.get(), code, i18nHelper);
    }

    private void storeOtp(String verificationId, OtpVerificationDTO dto, long ttl, TimeUnit unit) {
        String key = buildKey(verificationId);
        var ops = redisTemplate.opsForHash();
        ops.put(key, "userId", dto.getUserId());
        ops.put(key, "cbPayId", dto.getCbPayId());
        ops.put(key, "reason", dto.getReason());
        ops.put(key, "otp", dto.getOtp());
        ops.put(key, "method", dto.getMethod());
        redisTemplate.expire(key, ttl, unit);
    }

    public OtpVerificationDTO getOtp(String verificationId) {
        String key = buildKey(verificationId);
        var ops = redisTemplate.opsForHash();

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return null;
        }
        String userId = (String) ops.get(key, "userId");
        String cbPayId = (String) ops.get(key, "cbPayId");
        String reason = (String) ops.get(key, "reason");
        String otp = (String) ops.get(key, "otp");
        String method = (String) ops.get(key, "method");
        return new OtpVerificationDTO(userId, cbPayId, reason, otp, method);
    }

    public void deleteOtp(String verificationId) {
        redisTemplate.delete(buildKey(verificationId));
    }

    private String buildKey(String verificationId) {
        return OTP_PREFIX + verificationId;
    }

    private String createVerificationSession(String userId, String cbPayId, String reason, long ttl, TimeUnit unit, Object metadata) {
        var sessionDTO = new VerificationSessionDTO(userId, cbPayId, reason,metadata);
        return verificationSessionService.createSession(sessionDTO, ttl, unit);
    }


}