package mvc.screens;

import javafx.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


import mvc.gateway.PersonGateway;
import mvc.model.AuditTrail;
import mvc.model.Person;
import mvc.services.PersonService;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jni.Local;
import org.jboss.jandex.Main;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static mvc.gateway.PersonGateway.fetchAuditTrail;

public class PersonDetailController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private TextField personFirstName;

    @FXML
    private TextField personAge;

    @FXML
    private TextField personDOB;

    LocalDateTime lastModified;

    @FXML
    private TextField personLastName;

    @FXML
    private TextField personID;

    private ArrayList<AuditTrail> auditTrailList;

    @FXML
    private ListView<AuditTrail> AuditList;

    private Person person;

    public PersonDetailController(Person person) {
        this.person = person;
    }

    @FXML
    void cancelSave(ActionEvent event) {

        //LOGGER.info("Cancel clicked");
        MainController.getInstance().switchView(ScreenType.PERSONLIST);
    }

    @FXML
    void save(ActionEvent event) {
        //LOGGER.info("Save clicked");
        int flag = 0;
        // TODO: validate text fields FIRST before you save them to model
        if(person.getFirstName() == null || person.getFirstName().isEmpty() && person.getLastName() == null || person.getLastName().isEmpty())
            flag = 1;
        Person tempPerson = new Person();

        String token ="";

        // TODO: Make this dumb flag determining if we are updating or creating better implemented...
        if(flag == 0) {
            LOGGER.info("UPDATING " + person);
            if(personFirstName.getText().isEmpty() || personFirstName.getText().length() > 100)
            {
                LOGGER.error("First name must be between 1 and 100 characters");
                return;
            }
            if(personLastName.getText().isEmpty() || personLastName.getText().length() > 100)
            {
                LOGGER.error("Last name must be between 1 and 100 characters");
                return;
            }
            try {
                // Obscure date checking mumbo jumbo to verify validity (e.g. if its past the current date)
                tempPerson.setDob(LocalDate.parse(personDOB.getText()));
                tempPerson.setFirstName(personFirstName.getText());
                tempPerson.setLastName(personLastName.getText());
                tempPerson.setId(Integer.parseInt(personID.getText()));
                if(tempPerson.getDob().isAfter(LocalDate.now()) || tempPerson.getDob() == null)
                    LOGGER.error("Please enter a date before: "+ LocalDate.now().toString());
                JSONObject tempObj3 = new JSONObject(MainController.getInstance().getSession().getSessionID());
                //System.out.println("PersonDetail: temp person = " + tempPerson.toString());
                //System.out.println("PersonDetail: tempObj3.getString = " + tempObj3.getString("id"));
                Person tempPersonTimestamp = PersonGateway.fetchPerson(MainController.getInstance().getSession().getSessionID(), tempPerson.getId());
                System.out.println("tempPersonTimestamp == " + tempPersonTimestamp.getLastModified());
                if(tempPersonTimestamp.getLastModified().toString().equals(lastModified.toString())) {
                    System.out.println("TIMESTAMPS ARE THE SAME");
                    PersonGateway.updatePerson(tempObj3.getString("id"), tempPerson);
                }
                else
                    Alerts.infoAlert("Person has been modified by someone else!", "Please redo your changes and try to save again");


            } catch(DateTimeException e) {
                // TODO: find and plug in your alert helper functions
                // Alert errorAlert = new Alert(Alert.AlertType.ERROR)
                // Print an error describing the date format.
                LOGGER.error("Enter a valid date, YYYY-MM-DD!");
                return;
            }



            LOGGER.info("PERSON LIST AFTER UPDATE: " + MainController.getInstance().getPerson().toString());
        }



        else {

            if(personFirstName.getText().isEmpty() || personFirstName.getText().length() > 100)
            {
                LOGGER.error("First name must be between 1 and 100 characters");
                return;
            }
            if(personLastName.getText().isEmpty() || personLastName.getText().length() > 100)
            {
                LOGGER.error("Last name must be between 1 and 100 characters");
                return;
            }
            try {
                // Obscure date checking mumbo jumbo to verify validity (e.g. if its past the current date)

                if(LocalDate.parse(personDOB.getText()).isAfter(LocalDate.now()))
                    LOGGER.error("Please enter a date before: "+ LocalDate.now().toString());

            } catch(DateTimeException e) {
                // TODO: find and plug in your alert helper functions
                // Alert errorAlert = new Alert(Alert.AlertType.ERROR)
                // Print an error describing the date format.
                LOGGER.error("Enter a valid date, YYYY-MM-DD!");
                return;
            }
            tempPerson.setFirstName(personFirstName.getText());
            tempPerson.setLastName(personLastName.getText());
            tempPerson.setDob(LocalDate.parse(personDOB.getText()));
            LOGGER.info("CREATING " + tempPerson);
            //System.out.println("ADD PERSON = " + tempPerson.toString());
            JSONObject tempObj3 = new JSONObject(MainController.getInstance().getSession().getSessionID());
            PersonGateway.addPerson(tempObj3.getString("id"), tempPerson);
            LOGGER.info("PERSON LIST AFTER INSERTING = " + MainController.getInstance().getPerson().toString());
        }
        // Switch back to the person list view after saving
        MainController.getInstance().switchView(ScreenType.PERSONLIST);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // this is where we connect the model data to the GUI components like text fields
        personFirstName.setText(person.getFirstName());
        personLastName.setText(person.getLastName());
        auditTrailList = fetchAuditTrail(MainController.getInstance().getSession().getSessionID(), person, "http://localhost:8080");
        ObservableList<AuditTrail> tempList = FXCollections.observableArrayList(auditTrailList);
        AuditList.setItems(tempList);
        //personAge.setText("" + person.getAge()); <= This is unused currently...
        Person tempPerson404 = PersonGateway.fetchPerson(MainController.getInstance().getSession().getSessionID(), person.getId());
        lastModified = tempPerson404.getLastModified();
        System.out.println("LAST MODIFIED == " + lastModified);
        if(person.getDob() == null)
            personDOB.setText("");
        else
            personDOB.setText(person.getDob().toString());

        if(person.getId() == null)
            personID.setText("");
        else
            personID.setText(person.getId().toString());

    }


}
