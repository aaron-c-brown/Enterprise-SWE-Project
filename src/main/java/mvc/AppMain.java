package mvc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mvc.model.Person;
import mvc.screens.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import repository.UserRepository;

import java.util.ArrayList;

@SpringBootApplication
@ComponentScan({"mvc.services"})
@EntityScan("mvc.model")
@EnableJpaRepositories("repository")
public class AppMain extends Application { //public class AppMain extends Application {
    private static final Logger LOGGER = LogManager.getLogger();

    private ArrayList<Person> people;

    public static void main(String[] args) {
        LOGGER.info("before launch");
        SpringApplication.run(AppMain.class, args);
        launch(args);
        LOGGER.info("after launch");

    }

    @Override
    public void init() throws Exception {
        super.init();
        LOGGER.info("in init");
        people = new ArrayList<>();
        //people.add(new Person(1, "John", "Smith",  LocalDate.of(2000,1,1)));
        //people.add(new Person(2, "Jane", "Doe",  LocalDate.of(1989, 4,21)));
        //people.add(new Person(3, "Joe", "Mama",  LocalDate.of(1300,6,10)));

    }

    @Override
    public void stop() throws Exception {
        //super.stop();
        LOGGER.info("in stop");
    }

    @Override
    public void start(Stage stage) throws Exception {
        //LOGGER.info("before start");

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/main_view.fxml"));
        loader.setController(MainController.getInstance());

        //MainController.getInstance().setPeople(people);
        Parent rootNode = loader.load();
        stage.setScene(new Scene(rootNode));

        stage.setTitle("Application");
        stage.show();

        LOGGER.info("after start");
    }
}
