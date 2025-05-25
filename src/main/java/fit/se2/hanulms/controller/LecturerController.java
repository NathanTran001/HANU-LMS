package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.*;
import fit.se2.hanulms.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LecturerController {

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

    // ============== LECTURER ENDPOINTS ==============

    @GetMapping("/lecturers")
    public ResponseEntity<List<Lecturer>> getLecturers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String faculty) {
        try {
            List<Lecturer> lecturers = lecturerRepository.findAll();

            // Apply filters if provided
            if (search != null && !search.trim().isEmpty()) {
                lecturers = lecturers.stream()
                        .filter(l -> l.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                (l.getName() != null && l.getName().toLowerCase().contains(search.toLowerCase())))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(lecturers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lecturers/{id}")
    public ResponseEntity<Lecturer> getLecturer(@PathVariable Long id) {
        try {
            Optional<Lecturer> lecturer = lecturerRepository.findById(id);
            if (lecturer.isPresent()) {
                return ResponseEntity.ok(lecturer.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/lecturers")
    public ResponseEntity<?> createLecturer(@RequestBody Map<String, Object> request) {
        try {
            // Extract and validate data from request
            String username = (String) request.get("username");
            String name = (String) request.get("name");
            String email = (String) request.get("email");

            // Basic validation
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username is required"));
            }

            // Validate if username already exists
            if (lecturerRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username already exists"));
            }

            Lecturer lecturer = new Lecturer();
            lecturer.setUsername(username);
            lecturer.setName(name);
            lecturer.setEmail(email);

            Lecturer savedLecturer = lecturerRepository.save(lecturer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLecturer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create lecturer"));
        }
    }

    @PutMapping("/lecturers/{id}")
    public ResponseEntity<?> updateLecturer(@PathVariable Long id,
                                            @RequestBody Map<String, Object> request) {
        try {
            Optional<Lecturer> optionalLecturer = lecturerRepository.findById(id);
            if (!optionalLecturer.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Lecturer lecturer = optionalLecturer.get();

            // Update only provided fields
            if (request.containsKey("name")) {
                lecturer.setName((String) request.get("name"));
            }
            if (request.containsKey("email")) {
                lecturer.setEmail((String) request.get("email"));
            }

            Lecturer updatedLecturer = lecturerRepository.save(lecturer);
            return ResponseEntity.ok(updatedLecturer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update lecturer"));
        }
    }

    @DeleteMapping("/lecturers/{id}")
    public ResponseEntity<?> deleteLecturer(@PathVariable Long id) {
        try {
            Optional<Lecturer> optionalLecturer = lecturerRepository.findById(id);
            if (!optionalLecturer.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            lecturerRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Lecturer deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete lecturer"));
        }
    }


    // ============== FACULTY ENDPOINTS ==============

    @GetMapping("/faculties")
    public ResponseEntity<List<Faculty>> getFaculties(
            @RequestParam(required = false) String search) {
        try {
            List<Faculty> faculties = facultyRepository.findAll();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                faculties = faculties.stream()
                        .filter(f -> f.getCode().toLowerCase().contains(lowerCaseSearch) ||
                                f.getName().toLowerCase().contains(lowerCaseSearch))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/faculties/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable String id) {
        try {
            Optional<Faculty> faculty = facultyRepository.findById(id);
            if (faculty.isPresent()) {
                return ResponseEntity.ok(faculty.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/faculties")
    public ResponseEntity<?> createFaculty(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            String name = (String) request.get("name");

            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Faculty code is required"));
            }

            if (facultyRepository.findById(code).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Faculty code already exists"));
            }

            Faculty faculty = new Faculty();
            faculty.setCode(code);
            faculty.setName(name);

            Faculty savedFaculty = facultyRepository.save(faculty);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFaculty);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create faculty"));
        }
    }

    @PutMapping("/faculties/{id}")
    public ResponseEntity<?> updateFaculty(@PathVariable String id,
                                           @RequestBody Map<String, Object> request) {
        try {
            Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
            if (!optionalFaculty.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Faculty faculty = optionalFaculty.get();

            if (request.containsKey("name")) {
                faculty.setName((String) request.get("name"));
            }

            Faculty updatedFaculty = facultyRepository.save(faculty);
            return ResponseEntity.ok(updatedFaculty);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update faculty"));
        }
    }

    @DeleteMapping("/faculties/{id}")
    public ResponseEntity<?> deleteFaculty(@PathVariable String id) {
        try {
            Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
            if (!optionalFaculty.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            facultyRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Faculty deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete faculty"));
        }
    }

    // ============== ENROLLMENT ENDPOINT ==============

    @PostMapping("/courses/{courseCode}/enroll")
    public ResponseEntity<?> enrollStudent(@PathVariable String courseCode,
                                           @RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String enrolmentKey = (String) request.get("enrolmentKey");

            Optional<Course> optionalCourse = courseRepository.findById(courseCode);
            if (!optionalCourse.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Student> optionalStudent = studentRepository.findById(studentId);
            if (!optionalStudent.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Student not found"));
            }

            Course course = optionalCourse.get();
            Student student = optionalStudent.get();

            if (!course.getEnrolmentKey().equals(enrolmentKey)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid enrollment key"));
            }

            if (course.getStudents().contains(student)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Student already enrolled"));
            }

            student.getCourses().add(course);
            course.getStudents().add(student);

            studentRepository.save(student);
            courseRepository.save(course);

            return ResponseEntity.ok(Map.of("message", "Successfully enrolled"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to enroll student"));
        }
    }

}