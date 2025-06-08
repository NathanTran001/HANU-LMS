package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacultyRepository extends JpaRepository<Faculty, String> {
    @Query("SELECT f FROM Faculty f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) OR LOWER(f.code) LIKE LOWER(CONCAT('%', :searchPhrase, '%'))")
    Page<Faculty> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
            @Param("searchPhrase") String searchPhrase,
            Pageable pageable);
}
