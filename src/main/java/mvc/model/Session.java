package mvc.model;

import javax.persistence.*;

@Table(name = "session", indexes = {
        @Index(name = "user_id", columnList = "user_id")
})
@Entity
public class Session {
    @Id
    @Column(name = "token", nullable = false, length = 64)
    private String id;

    @ManyToOne(optional = false, cascade=CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Session{" + "id=" + id + ", user='" + user + '\'' + '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}