package mvc.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.Alerts;
import javafx.scene.control.Alert;
import mvc.model.AuditTrail;
import mvc.model.Person;
//import mvc.screens.MainController;
import mvc.screens.MainController;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import repository.AuditTrailRepository;
import repository.PersonRepository;
import repository.SessionRepository;
import repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class PersonService {
    private PersonRepository personRepository;
    private SessionRepository sessionRepository;


    private AuditTrailRepository auditRepository;

    public PersonService(PersonRepository personRepository, SessionRepository sessionRepository, AuditTrailRepository auditRepository)
    {
        this.personRepository = personRepository;
        this.sessionRepository = sessionRepository;
        this.auditRepository = auditRepository;
    }

    @GetMapping("/people")
    @ResponseBody
    public ResponseEntity<ArrayList<Person>> fetchPeople(@RequestHeader(required = false) String Authorization)
    {
        if(Authorization != null) {
            if (sessionRepository.existsById(Authorization)) {
                return new ResponseEntity<>((ArrayList<Person>) personRepository.findAll(), HttpStatus.OK);
            }
        }
        //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);


    }

    @PostMapping("/people")
    @ResponseBody
    public ResponseEntity<Object> addPerson(@RequestHeader(required = false) String Authorization, @RequestBody Person person)
    {

        if(sessionRepository.existsById(Authorization))
        {
            if(person.getFirstName() == null || person.getLastName() == null || person.getDob() == null)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
            }
            if(person.getFirstName().length() == 0 || person.getLastName().length() == 0 || person.getFirstName().length() > 100 || person.getLastName().length() > 100 || person.getDob().isAfter(LocalDate.now()))
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: First and Last name must be between 1-100, and Date of Birth cannot be after " + LocalDate.now());
            }

            person = personRepository.save(person);
            AuditTrail auditTrail = new AuditTrail();
            // Maybe make a function to actually implement transactions for this audit trail additon
            auditTrail.setChangedBy(MainController.getInstance().getUser());
            auditTrail.setPerson(person);
            auditTrail.setChangeMsg("added\n");
            auditTrail.setWhenOccurred(Instant.now());
            auditRepository.save(auditTrail);
            return ResponseEntity.status(HttpStatus.OK).body("OK");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token");
    }


    @PutMapping("/people/{id}")
    @ResponseBody
    public ResponseEntity<Object> updatePerson(@PathVariable("id") int id, @RequestHeader(required = false) String Authorization, @RequestBody Person person)
    {
        if(Authorization != null) {
            if (!personRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: ID not found");
            }
            if (sessionRepository.existsById(Authorization)) {
                Optional<Person> optionalPerson = personRepository.findById(id);

                if (optionalPerson.isPresent()) {
                    Person tempPerson = optionalPerson.get();

                    String changeMessage ="";
                    System.out.println(tempPerson.getFirstName() + "     " +person.getFirstName());
                    if(person.getFirstName().compareTo(tempPerson.getFirstName()) != 0)
                    {
                        changeMessage += "First Name changed from " + tempPerson.getFirstName() + " to " + person.getFirstName() +"\n";
                    }
                    if(person.getLastName().compareTo(tempPerson.getLastName()) != 0)
                    {
                        changeMessage += "Last Name changed from " + tempPerson.getLastName() + " to " + person.getLastName() +"\n";
                    }
                    if(person.getDob().compareTo(tempPerson.getDob()) != 0)
                    {
                        changeMessage += "Date of Birth changed from " + tempPerson.getDob() + " to " + person.getDob() +"\n";
                    }
                    person.setId(id);

                    // IMPORTANT:
                    // Since update is supposed to work regardless of the number of changes the user wants to make
                    // i.e: {"firstName":"Joe"} vs {"firstName":"Joe", "lastName":"Smith"}
                    // We can't just invalidate the users update request if one of the requests is mispelled
                    //   or otherwise not meeting our requirements. Instead of outright invalidating their request.
                    //   we will simply just not update the given field that is mispelled or otherwise incorrect, and
                    //   proceed with the other actually correct update fields.

                    if(person.getFirstName() == null && person.getLastName() == null && person.getDob() == null)
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data: First and Last name must be between 1-100, and Date of Birth cannot be after " + LocalDate.now());


                    person.setLastModified(LocalDateTime.now());
                    personRepository.save(person);


                    AuditTrail auditTrail = new AuditTrail();
                    auditTrail.setChangedBy(MainController.getInstance().getUser());
                    auditTrail.setPerson(person);
                    auditTrail.setChangeMsg(changeMessage);
                    auditTrail.setWhenOccurred(Instant.now());
                    auditRepository.save(auditTrail);


                }
                System.out.println("Person = " + person);

                return ResponseEntity.status(HttpStatus.OK).body("OK");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token");
    }

    @DeleteMapping("/people/{id}")
    @ResponseBody
    public ResponseEntity<Object> deletePerson(@PathVariable("id") int id, @RequestHeader(required = false) String Authorization)
    {
        if(Authorization != null) {
            if (sessionRepository.existsById(Authorization)) {
                if (!personRepository.existsById(id)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: ID not found");
                }
                auditRepository.deleteAuditTrailByPerson_Id(id);
                personRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body("OK");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token");
    }

    @GetMapping("/people/{id}")
    @ResponseBody
    public Person fetchPerson(@PathVariable("id") int id, @RequestHeader(required = false) String Authorization, HttpServletResponse response)
    {
        if(Authorization == null)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        if(sessionRepository.existsById(Authorization))
        {
            if(personRepository.existsById(id))
            {
                Optional<Person> tempPerson = personRepository.findById(id);
                if(tempPerson.isPresent())
                {
                    return tempPerson.get();
                }
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
    }

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public void setPersonRepository(PersonRepository personRepository) { this.personRepository = personRepository; }
}
