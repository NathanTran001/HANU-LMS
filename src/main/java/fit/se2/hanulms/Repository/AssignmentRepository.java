package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
}
