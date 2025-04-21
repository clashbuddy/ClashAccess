package studio.clashbuddy.clashaccess;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.clashbuddy.clashaccess.security.AuthorizedUser;
import studio.clashbuddy.clashaccess.security.RequireAccess;

@RestController
public class TestController {



    @RequireAccess(roles = "admin", excludedRoles = "employee")
    @GetMapping("/hello")
    public AuthorizedUser getData(AuthorizedUser authorizedUser){
        return authorizedUser;
    }

}
