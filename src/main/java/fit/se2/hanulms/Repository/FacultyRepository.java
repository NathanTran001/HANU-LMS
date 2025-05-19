package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, String> {
}
