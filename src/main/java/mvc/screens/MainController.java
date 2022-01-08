package mvc.screens;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import mvc.gateway.PersonGateway;
import mvc.gateway.SessionGateway;
import mvc.model.Person;
import mvc.model.User;
import mvc.services.PersonService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.PersonRepository;
import repository.SessionRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.DataInput;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MainController instance = null;

    private PersonRepository personRepository;
    private SessionRepository sessionRepository;

    private SessionGateway session;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private PersonService personService;
    private ArrayList<Person> people;
    private User user;

    @FXML
    private BorderPane rootPane;

    private MainController() {
        LOGGER.info("MainController created");
    }

    public void switchView(ScreenType screenType, Object... args)
    {
        people = new ArrayList<Person>();

        LOGGER.info(session);

        switch (screenType)
        {
            case LOGIN:
                // The LOGIN case isn't necessary as we have no way to go back to it presently
                //   but I feel we will probably need this implemented later, so hey.
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/login_screen.fxml"));
                loader.setController(new LoginController());
                Parent rootNode = null;
                try {
                    rootNode = loader.load();
                    rootPane.setCenter(rootNode);
                } catch (IOException e) {
                    LOGGER.error("Error loading login screen.");
                    e.printStackTrace();
                }
                break;
            case PERSONLIST:
                loader = new FXMLLoader(this.getClass().getResource("/person_list.fxml"));
                System.out.println("MAINCONTROLLER USER = " + user.getLogin());
                people = (ArrayList<Person>) PersonGateway.fetchPeople(session.getSessionID());

                // Make a HttpGet request thingie here, similar to the one found in LoginController zzz
                //personService.fetchPeople(auth);
                loader.setController(new PersonListController(people));
                rootNode = null;
                try {
                    rootNode = loader.load();
                    rootPane.setCenter(rootNode);
                } catch (IOException e) {
                    LOGGER.error("Error loading person list screen.");
                    e.printStackTrace();
                }
                break;
            case PERSONDETAIL:
                loader = new FXMLLoader(this.getClass().getResource("/person_detail.fxml"));
                if(!(args[0] instanceof Person)) {
                    throw new IllegalArgumentException("Hey that's not a Person! " + args[0].toString());
                }
                loader.setController(new PersonDetailController((Person) args[0]));
                rootNode = null;
                try {
                    rootNode = loader.load();
                    rootPane.setCenter(rootNode);
                } catch (IOException e) {
                    LOGGER.error("Error loading person detail screen.");
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switchView(ScreenType.LOGIN);
    }

    public static MainController getInstance() {
        if(instance == null)
            instance = new MainController();
        return instance;
    }

    // accessors

    public SessionGateway getSession() { return session; }

    public ArrayList<Person> getPerson() {
        return people;
    }

    public void setSession(SessionGateway session) {
        this.session = session;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }
}
