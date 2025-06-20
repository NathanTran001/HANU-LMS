package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends LMSUserRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
            "UPPER(s.name) LIKE UPPER(CONCAT('%', :searchPhrase, '%')) OR " +
            "UPPER(s.email) LIKE UPPER(CONCAT('%', :searchPhrase, '%'))")
    Page<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String searchPhrase, Pageable pageable);
}
