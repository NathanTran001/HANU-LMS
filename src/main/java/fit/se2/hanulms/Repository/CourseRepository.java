package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Course;
import fit.se2.hanulms.model.Lecturer;
import fit.se2.hanulms.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
    Page<Course> findAllByStudentsContaining(Student student, Pageable pageable);

    Page<Course> findAllByLecturersContaining(Lecturer lecturer, Pageable pageable);

    Page<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String searchPhrase, String searchPhrase1, Pageable pageable);
}
