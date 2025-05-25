package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.*;
import fit.se2.hanulms.model.Course;
import fit.se2.hanulms.model.Faculty;
import fit.se2.hanulms.model.Lecturer;
import fit.se2.hanulms.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    AssignmentRepository assignmentRepository;

    // ============== COURSE ENDPOINTS ==============

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String faculty,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Course> courses;

            if (userDetails != null && userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("LECTURER"))) {
                // Return courses for this lecturer
                Lecturer thisLecturer = lecturerRepository.findByUsername(userDetails.getUsername()).get();
                courses = courseRepository.findAll().stream()
                        .filter(c -> c.getLecturers().contains(thisLecturer))
                        .collect(Collectors.toList());
            } else if (userDetails != null && userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("STUDENT"))) {
                // Return courses for this student
                Student thisStudent = studentRepository.findByUsername(userDetails.getUsername()).get();
                courses = courseRepository.findAll().stream()
                        .filter(c -> c.getStudents().contains(thisStudent))
                        .collect(Collectors.toList());
            } else {
                courses = courseRepository.findAll();
            }

            // Apply search filter if provided
            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                courses = courses.stream()
                        .filter(c -> c.getCode().toLowerCase().contains(lowerCaseSearch) ||
                                c.getName().toLowerCase().contains(lowerCaseSearch) ||
                                (c.getDescription() != null && c.getDescription().toLowerCase().contains(lowerCaseSearch)))
                        .collect(Collectors.toList());
            }

            // Apply faculty filter if provided
            if (faculty != null && !faculty.trim().isEmpty()) {
                courses = courses.stream()
                        .filter(c -> c.getFaculty() != null && c.getFaculty().getCode().equals(faculty))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/courses/{code}")
    public ResponseEntity<Course> getCourse(@PathVariable String code) {
        try {
            Optional<Course> course = courseRepository.findById(code);
            if (course.isPresent()) {
                return ResponseEntity.ok(course.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/courses")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> createCourse(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            String enrolmentKey = (String) request.get("enrolmentKey");
            String facultyCode = (String) request.get("facultyCode");
            List<Long> lecturerIds = (List<Long>) request.get("lecturerIds");

            List<String> errorMessages = validateCourse(code, name, description, enrolmentKey, "create");

            if (!errorMessages.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
            }

            Course course = new Course();
            course.setCode(code);
            course.setName(name);
            course.setEnrolmentKey(enrolmentKey);
            course.setDescription(description);

            if (facultyCode != null) {
                Optional<Faculty> faculty = facultyRepository.findById(facultyCode);
                faculty.ifPresent(course::setFaculty);
            }

            if (lecturerIds != null && !lecturerIds.isEmpty()) {
                List<Lecturer> lecturers = lecturerRepository.findAllById(lecturerIds);
                course.setLecturers(lecturers);

                // Update lecturer's course list
                for (Lecturer lecturer : lecturers) {
                    lecturer.getCourses().add(course);
                }
            }

            Course savedCourse = courseRepository.save(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create course"));
        }
    }

    @PutMapping("/courses/{code}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> updateCourse(@PathVariable String code,
                                          @RequestBody Map<String, Object> request) {
        try {
            Optional<Course> optionalCourse = courseRepository.findById(code);
            if (!optionalCourse.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            String name = (String) request.get("name");
            String description = (String) request.get("description");
            String enrolmentKey = (String) request.get("enrolmentKey");
            String facultyCode = (String) request.get("facultyCode");
            List<Long> lecturerIds = (List<Long>) request.get("lecturerIds");

            List<String> errorMessages = validateCourse(code, name, description, enrolmentKey, "edit");

            if (!errorMessages.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
            }

            Course course = optionalCourse.get();

            // Clear existing lecturer associations
            for (Lecturer lecturer : course.getLecturers()) {
                lecturer.getCourses().remove(course);
                lecturerRepository.save(lecturer);
            }
            course.getLecturers().clear();

            // Update course fields
            course.setName(name);
            course.setEnrolmentKey(enrolmentKey);
            course.setDescription(description);

            if (facultyCode != null) {
                Optional<Faculty> faculty = facultyRepository.findById(facultyCode);
                faculty.ifPresent(course::setFaculty);
            }

            if (lecturerIds != null && !lecturerIds.isEmpty()) {
                List<Lecturer> lecturers = lecturerRepository.findAllById(lecturerIds);
                course.setLecturers(lecturers);

                // Update lecturer's course list
                for (Lecturer lecturer : lecturers) {
                    lecturer.getCourses().add(course);
                }
            }

            Course updatedCourse = courseRepository.save(course);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update course"));
        }
    }

    @DeleteMapping("/courses/{code}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> deleteCourse(@PathVariable String code) {
        try {
            Optional<Course> optionalCourse = courseRepository.findById(code);
            if (!optionalCourse.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Course course = optionalCourse.get();

            // Clear associations before deletion
            for (Lecturer lecturer : course.getLecturers()) {
                lecturer.getCourses().remove(course);
                lecturerRepository.save(lecturer);
            }

            for (Student student : course.getStudents()) {
                student.getCourses().remove(course);
                studentRepository.save(student);
            }

            courseRepository.delete(course);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete course"));
        }
    }

    @GetMapping("/courses/faculty/{facultyId}")
    public ResponseEntity<List<Course>> getCoursesByFaculty(@PathVariable String facultyId) {
        try {
            Optional<Faculty> faculty = facultyRepository.findById(facultyId);
            if (!faculty.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            List<Course> courses = courseRepository.findAll().stream()
                    .filter(c -> c.getFaculty() != null && c.getFaculty().getCode().equals(facultyId))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============== HELPER METHODS ==============

    private List<String> validateCourse(String courseCode, String courseName,
                                        String description, String enrolmentKey, String mode) {
        List<String> errorMessages = new ArrayList<>();

        if (mode.equals("create")) {
            if (courseRepository.existsById(courseCode)) {
                errorMessages.add("The entered course code already exists!");
            }
        }

        if (courseCode != null && courseCode.length() > 10) {
            errorMessages.add("The maximum length for course code is 10 characters!");
        }
        if (courseName != null && courseName.length() > 40) {
            errorMessages.add("The maximum length for course name is 40 characters!");
        }
        if (description != null && description.length() > 254) {
            errorMessages.add("The maximum length for course description is 254 characters!");
        }
        if (enrolmentKey != null && enrolmentKey.length() > 10) {
            errorMessages.add("The maximum length for enrolment key is 10 characters!");
        }

        return errorMessages;
    }

}
