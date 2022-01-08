package mvc.screens;

import javafx.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


import mvc.gateway.PersonGateway;
import mvc.model.Person;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PersonListController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private ArrayList<Person> people;

    @FXML
    private ListView<Person> personList;


    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button firstButton;

    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    @FXML
    private Button lastButton;

    public PersonListController(ArrayList<Person> people) {
        this.people = people;
    }

    @FXML
    void clickPerson(MouseEvent event)
    {
        // Check if the MouseEvent is a double click
        if(event.getClickCount() == 2)
        {
            // on double click only
            // 1. get the model that is double clicked (if none, then bail)
            // 2. switch to the person editing screen with the model that is selected
            Person person = personList.getSelectionModel().getSelectedItem(); // basically pulls the data from the clicked index?
            if(person != null)
            {
                LOGGER.info("READING " + person);
                MainController.getInstance().switchView(ScreenType.PERSONDETAIL, person);
            }

        }
        //LOGGER.info("clicked a person");
    }

    @FXML
    void addPerson(ActionEvent event)
    {
        //LOGGER.info("add person clicked");

        // load the person detail with an empty person
        // call the main controller switch view method
        // the date is set at 2020/1/1 because I'm not sure how to set an "empty" date"
        MainController.getInstance().switchView(ScreenType.PERSONDETAIL, new Person());
    }

    @FXML
    void deletePerson(ActionEvent event)
    {
        // 0 is the lowest index we can go to, so anything above it would be valid to delete from, granted
        //   that there is something in the index.

        if(personList.getSelectionModel().getSelectedIndex() != -1)
        {
            Person tempPerson = personList.getSelectionModel().getSelectedItem();
            System.out.println("PERSON ID = " + tempPerson.getId());
            String tempUrl = "http://localhost:8080/people" + "/" + tempPerson.getId();
            String token = MainController.getInstance().getSession().getSessionID();
            JSONObject tempObj3 = new JSONObject(token);


            PersonGateway.deletePerson(tempObj3.getString("id"), tempPerson, tempUrl);
            MainController.getInstance().switchView(ScreenType.PERSONLIST, MainController.getInstance().getPerson());
            LOGGER.info("DELETING " + personList.getSelectionModel().getSelectedItem());
            LOGGER.info("PERSON LIST AFTER DELETE " + MainController.getInstance().getPerson().toString());

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // 1. turn plain ol arraylist of models into an ObservableArrayList
        ObservableList<Person> tempList = FXCollections.observableArrayList(people);

        // 2. plug the observable array list into the list
        personList.setItems(tempList);


    }
}
