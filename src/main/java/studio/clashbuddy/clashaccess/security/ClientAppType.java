package studio.clashbuddy.clashaccess.security;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ClientAppType {
    AGENT_APP(UserRole.AGENT),
    AGENT_DESKTOP(UserRole.AGENT),
    SUPER_ADMIN_WEB(UserRole.SUPER_ADMIN,UserRole.DEV),
    ADMIN_WEB(UserRole.SUPER_ADMIN,UserRole.DEV),
    CUSTOMER_WEB(UserRole.CUSTOMER),
    CUSTOMER_APP(UserRole.CUSTOMER),
    CUSTOMER_DESKTOP(UserRole.CUSTOMER);
    private final UserRole[] roles;
    ClientAppType(UserRole ... roles) {
        this.roles = roles;
    }

    public static boolean isWeb(ClientAppType clientAppType) {
        var webClients = List.of(CUSTOMER_WEB,SUPER_ADMIN_WEB,ADMIN_WEB);
        return webClients.contains(clientAppType);
    }

    public Boolean isRoleAllowedClientAppType(UserRole userRole){
        return Arrays.asList(roles).contains(userRole);
    }
}
