package studio.clashbuddy.clashaccess.auth.authentication;

import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;

class ClashAccessDefaultAuthenticationProvider extends ClashAccessAuthenticationProvider {
    private static final ClashAccessDefaultAuthenticationProvider INSTANCE = new ClashAccessDefaultAuthenticationProvider();

    private ClashAccessDefaultAuthenticationProvider() {}

    public static ClashAccessDefaultAuthenticationProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public void authenticate(String encryptedPassword, String rawPassword) {
        if(!passwordEncoder().matches(rawPassword, encryptedPassword))
            throw new ClashAccessDeniedException(helper().i18n("{clashaccess.error.missing.invalid-credentials}"),403);
    }

    @Override
    public ClashToken issueToken(ClashAuthPayload payload, double accessExpireInMinutes, double refreshExpireInMinutes) {
            var jwtUtil = new JwtUtility(getSecret(),helper());
            var token = jwtUtil.generateJWT(payload.getUserId(),payload.getRoles(),payload.getPermissions(), accessExpireInMinutes, refreshExpireInMinutes);
            var accessToken = token.getFirst();
            var refreshToken = token.getSecond();
            return new ClashToken(accessToken,refreshToken,"Bearer");
        }


}
