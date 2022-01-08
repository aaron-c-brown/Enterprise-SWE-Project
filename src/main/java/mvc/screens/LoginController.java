package mvc.screens;

import com.mysql.cj.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.Alerts;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import mvc.gateway.LoginGateway;
import mvc.gateway.SessionGateway;
import mvc.model.HashUtils;
import mvc.model.Person;
import mvc.model.User;
import mvc.services.SessionService;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import repository.SessionRepository;
import repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private TextField usernameEntry;

    @FXML
    private BorderPane rootPane;

    @FXML
    private PasswordField passwordEntry;

    // idk
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SessionService sessionService;

    @FXML
    void Login(ActionEvent event) throws IOException {
        // Get the username and password from the textfields
        String username = usernameEntry.getText();
        String hashedPassword = HashUtils.getCryptoHash(this.passwordEntry.getText(), "SHA-256");

        LOGGER.info(this.passwordEntry.getText() + " hashes to " + hashedPassword);
        LOGGER.info(username + " " + this.passwordEntry.getText());

        //LOGGER.info( username + " LOGGED IN");


        try {

            LoginGateway loginGateway = new LoginGateway();
            loginGateway.login(usernameEntry.getText(), passwordEntry.getText());


        } catch(UnauthorizedException e) {
            Alerts.infoAlert("Login failed!", "Either your username or your password is incorrect");
            return;
        } catch(UnknownException e1) {
            Alerts.infoAlert("Login failed!", "Something very bad happened: " + e1.getMessage());
            return;
        }

        // Switch to PersonListView immediately upon clicking login button
        //MainController.getInstance().setSession(sessionService.);
        MainController.getInstance().switchView(ScreenType.PERSONLIST);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
