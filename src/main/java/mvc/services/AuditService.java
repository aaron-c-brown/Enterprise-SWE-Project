package mvc.services;


import mvc.model.AuditTrail;
import mvc.model.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import repository.AuditTrailRepository;
import repository.PersonRepository;
import repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@Component
@Service
public class AuditService {

    private PersonRepository personRepository;
    private SessionRepository sessionRepository;


    private AuditTrailRepository auditRepository;

    public AuditService(PersonRepository personRepository, SessionRepository sessionRepository, AuditTrailRepository auditRepository)
    {
        this.personRepository = personRepository;
        this.sessionRepository = sessionRepository;
        this.auditRepository = auditRepository;
    }



    @GetMapping("/people/{id}/audittrail")
    @ResponseBody
    public ResponseEntity<ArrayList<AuditTrail>> fetchAudit(@PathVariable("id") int id, @RequestHeader(required = false) String Authorization)
    {
        ArrayList<AuditTrail> auditTrails = new ArrayList<AuditTrail>();
        if(Authorization != null || !Authorization.isEmpty()) {
            if (sessionRepository.existsById(Authorization))
            {
                if(personRepository.existsById(id)) {
                    ArrayList<AuditTrail> tempList = auditRepository.findByPerson_Id(id);
                    for (int i = 0; i < tempList.size(); i++) {
                        auditTrails.add(tempList.get(i));
                    }

                    return new ResponseEntity<>(auditTrails, HttpStatus.OK);
                }
                else
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            else
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);


    }


}
