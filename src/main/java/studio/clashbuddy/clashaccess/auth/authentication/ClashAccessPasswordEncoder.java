package studio.clashbuddy.clashaccess.auth.authentication;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class ClashAccessPasswordEncoder {

    public String encode(String password) {
        return  BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean matches(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
