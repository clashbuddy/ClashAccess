package studio.clashbuddy.clashaccess.security;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Principal {
    private  String userId;
    private String credentialId;
    private String deviceId;
    private List<UserPermission> permissions;
    private UserRole role;
    private ClientAppType clientAppType;
}
