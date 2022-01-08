package mvc.model;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

@Table(name = "audit_trail", indexes = {
        @Index(name = "changed_by", columnList = "changed_by"),
        @Index(name = "person_id", columnList = "person_id")
})
@Entity
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "change_msg", nullable = false, length = 1000)
    private String changeMsg;

    @ManyToOne(optional = false)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "when_occurred", nullable = false)
    private Instant whenOccurred;


    public Instant getWhenOccurred() {
        return whenOccurred;
    }

    public void setWhenOccurred(Instant whenOccurred) {
        this.whenOccurred = whenOccurred;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeMsg() {
        return changeMsg;
    }

    public void setChangeMsg(String changeMsg) {
        this.changeMsg = changeMsg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.of("Z"));
        String formattedTime = formatter.format(whenOccurred);
        return "User: " + changedBy.getLogin() + " \nPerson ID: " + person.getId() + " \nDescription: " + changeMsg + " Timestamp: " + whenOccurred.toString();
    }


}