package studio.clashbuddy.clashaccess.security;

public enum UserRole {
    ADMIN, SUPER_ADMIN, AGENT, CUSTOMER,DEV,ALL_ROLES;

    public static boolean isAdmin(UserRole userRole) {
        return ADMIN.equals(userRole) || SUPER_ADMIN.equals(userRole);
    }
}
