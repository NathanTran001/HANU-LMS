package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface LecturerRepository extends LMSUserRepository<Lecturer, Long> {
    @Query("SELECT l FROM Lecturer l WHERE " +
            "UPPER(l.name) LIKE UPPER(CONCAT('%', :searchPhrase, '%')) OR " +
            "UPPER(l.email) LIKE UPPER(CONCAT('%', :searchPhrase, '%'))")
    Page<Lecturer> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String searchPhrase, Pageable pageable);
}
