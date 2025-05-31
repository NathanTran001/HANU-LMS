package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findByUsername(String username);
    @Query("SELECT l FROM Lecturer l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) OR LOWER(l.email) LIKE LOWER(CONCAT('%', :searchPhrase, '%'))")
    Page<Lecturer> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            @Param("searchPhrase") String searchPhrase,
            @Param("searchPhrase") String searchPhrase2,
            Pageable pageable);
}
