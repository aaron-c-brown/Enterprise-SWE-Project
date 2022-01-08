package mvc.gateway;

import mvc.model.AuditTrail;
import mvc.model.Person;
import mvc.screens.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import repository.AuditTrailRepository;
import repository.PersonRepository;
import repository.SessionRepository;

@RestController
public class AuditGateway {
    private PersonRepository personRepository;
    private SessionRepository sessionRepository;
    private AuditTrailRepository auditRepository;

    private static final Logger LOGGER = LogManager.getLogger();

    public AuditGateway(){

    }

    @Autowired
    AuditTrailRepository auditTrailRepository;

    // Unused because its basically implemented else where... The legacy remains
    public void personAddAudit(Person person)
    {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setPerson(person);
        auditTrail.setChangeMsg("added\n");
        auditTrail.setChangedBy(MainController.getInstance().getUser());
        auditTrailRepository.save(auditTrail);
    }



}
