package mvc.services;

import mvc.model.HashUtils;
import mvc.model.Session;
import mvc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import repository.AuditTrailRepository;
import repository.SessionRepository;
import repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
public class UserService {

    private UserRepository userRepository;

    private SessionRepository sessionRepository;

    private AuditTrailRepository auditRepository;

    public UserService(UserRepository userRepository, SessionRepository sessionRepository)
    {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    // Testing, can be removed later.
    /*@GetMapping("/login")
    @ResponseBody
    public List<User> fetchUsers() { return userRepository.findAll(); }*/

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
