package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :searchPhrase, '%'))")
    Page<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            @Param("searchPhrase") String searchPhrase,
            @Param("searchPhrase") String searchPhrase2,
            Pageable pageable);
}
