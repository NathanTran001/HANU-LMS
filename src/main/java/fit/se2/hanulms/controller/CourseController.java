package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.*;
import fit.se2.hanulms.model.*;
import fit.se2.hanulms.model.DTO.CourseDTO;
import fit.se2.hanulms.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    JwtUtil jwtUtil;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    AssignmentRepository assignmentRepository;

    // ============== COURSE ENDPOINTS ==============

    @GetMapping("/courses")
    public ResponseEntity<Page<CourseDTO>> getCourses(
            HttpServletRequest request,
            Pageable pageable) {
        String username = jwtUtil.extractUsername(jwtUtil.extractTokenFromCookie(request));

        // Check if user is a student
        Optional<Student> studentOpt = studentRepository.findByUsername(username);
        if (studentOpt.isPresent()) {
            Page<Course> courses = courseRepository.findAllByStudentsContaining(studentOpt.get(), pageable);
            return ResponseEntity.ok(courses.map(CourseDTO::new));
        }

        // Check if user is a lecturer
        Optional<Lecturer> lecturerOpt = lecturerRepository.findByUsername(username);
        if (lecturerOpt.isPresent()) {
            Page<Course> courses = courseRepository.findAllByLecturersContaining(lecturerOpt.get(), pageable);
            return ResponseEntity.ok(courses.map(CourseDTO::new));
        }

        // User not found - return empty page with proper message
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build();
    }

    // Shared endpoint for both students and lecturers to get course details
    @GetMapping("/courses/{code}")
    public ResponseEntity<CourseDTO> getCourse(HttpServletRequest request, @PathVariable String code) {
        String username = jwtUtil.extractUsername(jwtUtil.extractTokenFromCookie(request));
        Optional<Course> courseOpt = courseRepository.findById(code);
        if (courseOpt.isPresent()) {
            // Check if user is a student
            Optional<Student> studentOpt = studentRepository.findByUsername(username);
            if (studentOpt.isPresent()) {
                // Check if student is enrolled in the course
                if (courseOpt.get().getStudents().contains(studentOpt.get())) {
                    return ResponseEntity.ok(new CourseDTO(courseOpt.get()));
                }
            }
            // Check if user is a lecturer
            Optional<Lecturer> lecturerOpt = lecturerRepository.findByUsername(username);
            if (lecturerOpt.isPresent()) {
                // Check if lecturer is associated with the course
                if (courseOpt.get().getLecturers().contains(lecturerOpt.get())) {
                    return ResponseEntity.ok(new CourseDTO(courseOpt.get()));
                }
            }

            // No student or lecturer associated with the course
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/courses")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request) {
        try {
            // Validate basic course data
            List<String> errorMessages = validateCourse(request.getCode(), request.getName(),
                    request.getDescription(), request.getEnrolmentKey(), "create");

            // Validate faculty exists
            if (request.getFacultyCode() == null || request.getFacultyCode().trim().isEmpty()) {
                errorMessages.add("Faculty code is required");
            } else {
                Optional<Faculty> faculty = facultyRepository.findById(request.getFacultyCode());
                if (faculty.isEmpty()) {
                    errorMessages.add("Faculty with code '" + request.getFacultyCode() + "' not found");
                }
            }

            // Validate lecturers exist
            if (request.getLecturerIds() == null || request.getLecturerIds().isEmpty()) {
                errorMessages.add("At least one lecturer must be selected");
            } else {
                List<Lecturer> foundLecturers = lecturerRepository.findAllById(request.getLecturerIds());
                if (foundLecturers.size() != request.getLecturerIds().size()) {
                    errorMessages.add("One or more selected lecturers not found");
                }
            }

            if (!errorMessages.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
            }

            // Create course
            Course course = new Course();
            course.setCode(request.getCode());
            course.setName(request.getName());
            course.setEnrolmentKey(request.getEnrolmentKey());
            course.setDescription(request.getDescription());

            // Set faculty
            Optional<Faculty> faculty = facultyRepository.findById(request.getFacultyCode());
            if (faculty.isPresent()) {
                course.setFaculty(faculty.get());
            }

            // Set lecturers
            if (request.getLecturerIds() != null && !request.getLecturerIds().isEmpty()) {
                List<Lecturer> lecturers = lecturerRepository.findAllById(request.getLecturerIds());
                course.setLecturers(lecturers);

                // Update lecturer's course list (if bidirectional relationship)
                for (Lecturer lecturer : lecturers) {
                    if (lecturer.getCourses() == null) {
                        lecturer.setCourses(new ArrayList<>());
                    }
                    lecturer.getCourses().add(course);
                }
            }

            Course savedCourse = courseRepository.save(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create course: " + e.getMessage()));
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
            for (Lecturer academicUser : course.getLecturers()) {
                academicUser.getCourses().remove(course);
                lecturerRepository.save(academicUser);
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
                List<Lecturer> academicUsers = lecturerRepository.findAllById(lecturerIds);
                course.setLecturers(academicUsers);

                // Update lecturer's course list
                for (Lecturer academicUser : academicUsers) {
                    academicUser.getCourses().add(course);
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
            for (Lecturer academicUser : course.getLecturers()) {
                academicUser.getCourses().remove(course);
                lecturerRepository.save(academicUser);
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

//    @GetMapping("/courses/faculty/{facultyId}")
//    public ResponseEntity<List<Course>> getCoursesByFaculty(@PathVariable String facultyId) {
//        try {
//            Optional<Faculty> faculty = facultyRepository.findById(facultyId);
//            if (!faculty.isPresent()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            List<Course> courses = courseRepository.findAll().stream()
//                    .filter(c -> c.getFaculty() != null && c.getFaculty().getCode().equals(facultyId))
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(courses);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // Search coruse
    @GetMapping("/courses/search")
    public ResponseEntity<Page<CourseDTO>> searchCourses(@RequestParam String searchPhrase, Pageable pageable) {
        try {
            Page<Course> courses = courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                    searchPhrase, searchPhrase, pageable);
            return ResponseEntity.ok(courses.map(CourseDTO::new));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
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
