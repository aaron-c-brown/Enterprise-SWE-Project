package mvc.services;

import mvc.model.HashUtils;
import mvc.model.Session;
import mvc.model.User;
import mvc.screens.MainController;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import repository.AuditTrailRepository;
import repository.SessionRepository;
import repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;


@RestController
@Component
@Service
public class SessionService {

    // From testing with Postman I must have these autowired or else the Postman login request will fail.
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;
    private AuditTrailRepository auditRepository;



    public SessionService(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;

    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity login(@RequestBody User user)
    {

        String seshToken = "";

        //String hashPass;
        //hashPass = HashUtils.getCryptoHash(user.getPassword(), "SHA-256");
        String tempName = user.getLogin();
        String tempPass = user.getPassword();
        tempPass = HashUtils.getCryptoHash(user.getPassword(), "SHA-256");

        if(userRepository.existsByLoginAndPassword(tempName, tempPass))
        {
            // generate seshtoken
            User tempUser = userRepository.findIdByLoginAndPassword(tempName, tempPass);

            seshToken = HashUtils.getCryptoHash(Timestamp.from(Instant.now()).toString(), "SHA-256");

            System.out.println("SeshToken = " + seshToken);
            user.setPassword(tempPass);
            Session session = new Session();
            session.setId(seshToken);
            session.setUser(tempUser);
            MainController.getInstance().setUser(tempUser);
            System.out.println("SessionService tempUser = " + tempUser);

            sessionRepository.save(session);



            String jsonSesh = "{ \"id\":"+ "\"" + seshToken+ "\"}";
            JSONObject jsonObj = new JSONObject(jsonSesh);

            // add it to session table
            return new ResponseEntity(jsonSesh, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    public SessionRepository getSessionRepository() {
        return sessionRepository;
    }

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void saveSession(Session session)
    {
        this.sessionRepository.save(session);
    }
}
