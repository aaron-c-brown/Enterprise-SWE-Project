package repository;

import mvc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLoginAndPassword(String login, String password);
    int findByLoginAndPassword(String login, String password);
    User findIdByLoginAndPassword(String login, String password);


}
