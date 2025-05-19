package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}
