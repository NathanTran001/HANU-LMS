package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Course;
import fit.se2.hanulms.model.Lecturer;
import fit.se2.hanulms.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, String> {
    Page<Course> findAllByStudentsContaining(Student student, Pageable pageable);

    Page<Course> findAllByLecturersContaining(Lecturer lecturer, Pageable pageable);

    Page<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String searchPhrase, String searchPhrase1, Pageable pageable);

    //    Page<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseAndFacultyCode(
//            String name, String code, String facultyCode, Pageable pageable);
    @Query("SELECT c FROM Course c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :searchPhrase, '%'))) AND c.faculty.code = :facultyCode")
    Page<Course> searchCoursesByFaculty(@Param("searchPhrase") String searchPhrase,
                                        @Param("facultyCode") String facultyCode,
                                        Pageable pageable);
}
