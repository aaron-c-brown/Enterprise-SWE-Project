package mvc.model;

import com.sun.istack.NotNull;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Table(name = "people")
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 100)
    @NotNull
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    @NotNull
    private String lastName;

    @Column(name = "dob", nullable = false)
    @NotNull
    private LocalDate dob;

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Column(name = "last_modified", nullable = false)
    @NotNull
    private LocalDateTime lastModified;

    @Override
    public String toString() {
        return firstName + " " + lastName + ", \tBirth Date: " + dob + ", \tAge: " + calcAge() +", \tID: " + id;
    }
    public String toJson() {
        return "{\"firstName\":\"" + firstName + "\"," + "\"lastName\":\"" + lastName + "\",\"dob\":\"" + dob + "\"}";
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int calcAge()
    {
        return Period.between(this.dob, LocalDate.now()).getYears();
    }


}