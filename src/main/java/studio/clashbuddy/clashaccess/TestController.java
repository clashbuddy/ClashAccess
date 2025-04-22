package studio.clashbuddy.clashaccess;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.clashbuddy.clashaccess.metadata.AvailableEndPoints;
import studio.clashbuddy.clashaccess.security.AuthorizedUser;
import studio.clashbuddy.clashaccess.security.RequireAccess;

import java.util.Map;

@RequestMapping("/api")
@RestController
public class TestController {
    private final AvailableEndPoints availableEndPoints;

    public TestController(AvailableEndPoints availableEndPoints) {
        this.availableEndPoints = availableEndPoints;
    }


    @RequireAccess(roles = "admin", excludedRoles = "employee", permissions = "Cookie")
    @GetMapping("/hello")
    public Map<String, Object> getData(AuthorizedUser authorizedUser){
        return Map.of("authorizedUser", authorizedUser, "availableEndPoints", availableEndPoints.getMetaEndpoints());
    }

    @PostMapping("/haaaaa")
    public void postData(){

    }

}
