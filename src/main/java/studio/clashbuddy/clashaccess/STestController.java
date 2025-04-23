package studio.clashbuddy.clashaccess;

import org.springframework.web.bind.annotation.*;
import studio.clashbuddy.clashaccess.security.AuthorizedUser;
import studio.clashbuddy.clashaccess.security.RequireAccess;

import java.util.Map;

@RequestMapping("/apis")
@RestController
public class STestController {


    @RequireAccess(roles = "admin", excludedRoles = "employee", permissions = "Cookie")
    @GetMapping("/hello")
    public Map<String, Object> getData(AuthorizedUser authorizedUser){
        return Map.of("authorizedUser", authorizedUser, "availableEndPoints","");
    }

    @PostMapping("/haaaaa")
    public void postData(){

    }

    @PutMapping
    public void changeData(){

    }

}
