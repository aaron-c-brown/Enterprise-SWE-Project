package repository;

import mvc.model.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Integer> {


    ArrayList<AuditTrail> findByPerson_Id(int id);

    @Transactional
    public void deleteAuditTrailByPerson_Id(int id);

}
