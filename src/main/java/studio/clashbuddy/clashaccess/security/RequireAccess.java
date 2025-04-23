package studio.clashbuddy.clashaccess.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAccess {
    String[] roles() default {};

    String[] excludedRoles() default {};

    String[] permissions() default {};

    String[] excludedPermissions() default {};

    String[] extraSecurityAttributes() default {};
}
