/*package mvc.model;

import com.sun.istack.NotNull;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Table(name="people")
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    private int ID;

    @Column(name ="first_name", length=64)
    @NotNull
    private String firstName;

    @Column(name="last_name", length=64)
    @NotNull
    private String lastName;

    @Column(name="dob")
    @NotNull
    private LocalDate birthDate;

    // Used later...
    private int age;

    public Person() {

    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public int getID() {
        return ID;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }


    public Person(int id, String firstName, String lastName, LocalDate date)//, LocalDate date)
    {
        this.ID = id;
        this.firstName = firstName;
        this.lastName = lastName;
        //this.age = age;
        this.birthDate = date;
    }

    public Person(String firstName, String lastName, LocalDate date)//, LocalDate date)
    {
        //this.ID = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = Period.between(date, LocalDate.now()).getYears();
        System.out.println("AGE = " + age);
        this.birthDate = date;
    }

    public static Person fromJSONObject(JSONObject json)
    {

        try {
            // I need to figure out how to handle passing in Dates
            Person person = new Person(json.getInt("id"), json.getString("first_name"), json.getString("last_name"), LocalDate.parse(json.getString("dob")));
            return person;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse a person from provided Json: " + json.toString());
        }

        // Below is temp stuff so IntelliJ doesn't yell at me
        //Person person = new Person(9999, "Joseph", "Mama", LocalDate.of(1300,6,10));
        //return person;
    }


    @Override
    public String toString() {
        return "Person{"+"id="+ID+", first_name="+firstName+'\''+'}';
    }
}*/