package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.*;
import fit.se2.hanulms.model.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PasswordEncoder p;

    // ==================== LECTURERS ====================
    @GetMapping("/lecturers")
    public ResponseEntity<Page<Lecturer>> listLecturer(Pageable pageable) {

        return ResponseEntity.ok(lecturerRepository.findAll(pageable));
    }

    @GetMapping("/lecturers/{id}")
    public ResponseEntity<?> getLecturer(@PathVariable Long id) {
        Optional<Lecturer> existing = lecturerRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Lecturer not found"));
        }

        return ResponseEntity.ok(existing.get());
    }

    @PostMapping("/lecturers")
    public ResponseEntity<?> createLecturer(@Valid @RequestBody UserTemplate userTemplate) {
        // Check username uniqueness across all users (lecturers, students, admins)
        if (lecturerRepository.existsByUsername(userTemplate.getUsername()) ||
                studentRepository.existsByUsername(userTemplate.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username already exists"));
        }

        Lecturer newLecturer = new Lecturer(userTemplate, p);
        return ResponseEntity.status(HttpStatus.CREATED).body(lecturerRepository.save(newLecturer));
    }

    @PutMapping("/lecturers/{id}")
    public ResponseEntity<?> editLecturer(@PathVariable Long id, @Valid @RequestBody UserTemplate userTemplate) {
        Optional<Lecturer> chosenLecturer = lecturerRepository.findById(id);
        if (chosenLecturer.isEmpty()) return ResponseEntity.notFound().build();

        Lecturer lecturer = chosenLecturer.get();
        lecturer.setName(userTemplate.getName());
        lecturer.setEmail(userTemplate.getEmail());

        // Only update password if provided
        if (userTemplate.getPassword() != null && !userTemplate.getPassword().isEmpty()) {
            lecturer.setPassword(p.encode(userTemplate.getPassword()));
        }

        lecturer.setFaculty(userTemplate.getFaculty());
        return ResponseEntity.ok(lecturerRepository.save(lecturer));
    }

    @DeleteMapping("/lecturers/{id}")
    public ResponseEntity<?> deleteLecturer(@PathVariable Long id) {
        Optional<Lecturer> lecturerOpt = lecturerRepository.findById(id);
        if (lecturerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lecturer lecturer = lecturerOpt.get();

        // Remove lecturer from all courses
        for (Course c : lecturer.getCourses()) {
            c.getLecturers().remove(lecturer);
            courseRepository.save(c);
        }
        lecturer.getCourses().clear();

        lecturerRepository.delete(lecturer);
        return ResponseEntity.ok().build();
    }

    // ==================== STUDENTS ====================
    @GetMapping("/students")
    public ResponseEntity<Page<Student>> listStudent(Pageable pageable) {
        return ResponseEntity.ok(studentRepository.findAll(pageable));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        Optional<Student> existing = studentRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not found"));
        }

        return ResponseEntity.ok(existing.get());
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@Valid @RequestBody UserTemplate userTemplate) {
        // Check username uniqueness across all users
        if (lecturerRepository.existsByUsername(userTemplate.getUsername()) ||
                studentRepository.existsByUsername(userTemplate.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username already exists"));
        }

        Student newStudent = new Student(userTemplate, p);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentRepository.save(newStudent));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> editStudent(@PathVariable Long id, @Valid @RequestBody UserTemplate userTemplate) {
        Optional<Student> selectedStudent = studentRepository.findById(id);
        if (selectedStudent.isEmpty()) return ResponseEntity.notFound().build();

        Student student = selectedStudent.get();
        student.setName(userTemplate.getName());
        student.setEmail(userTemplate.getEmail());

        // Only update password if provided
        if (userTemplate.getPassword() != null && !userTemplate.getPassword().isEmpty()) {
            student.setPassword(p.encode(userTemplate.getPassword()));
        }

        student.setFaculty(userTemplate.getFaculty());
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();

        // Remove student from all courses
        for (Course c : student.getCourses()) {
            c.getStudents().remove(student);
            courseRepository.save(c);
        }
        student.getCourses().clear();

        studentRepository.delete(student);
        return ResponseEntity.ok().build();
    }

    // ==================== FACULTIES ====================
    @GetMapping("/faculties")
    public ResponseEntity<Page<Faculty>> listFaculty(Pageable pageable) {
        return ResponseEntity.ok(facultyRepository.findAll(pageable));
    }

    @GetMapping("/faculties/all")
    public ResponseEntity<List<Faculty>> listFacultyAll() {
        return ResponseEntity.ok(facultyRepository.findAll());
    }

    @GetMapping("/faculties/{code}")
    public ResponseEntity<?> getFaculty(@PathVariable String code) {
        Optional<Faculty> existing = facultyRepository.findById(code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Faculty not found"));
        }

        return ResponseEntity.ok(existing.get());
    }

    @PostMapping("/faculties")
    public ResponseEntity<?> createFaculty(@Valid @RequestBody Faculty faculty) {
        if (facultyRepository.existsById(faculty.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", "Faculty code already exists"));
        }
        Faculty saved = facultyRepository.save(faculty);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "faculty", saved, "message", "Faculty created successfully"));
    }

    @PutMapping("/faculties/{code}")
    public ResponseEntity<?> editFaculty(@PathVariable String code, @Valid @RequestBody Faculty faculty) {
        Optional<Faculty> existing = facultyRepository.findById(code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Faculty not found"));
        }

        Faculty existingFaculty = existing.get();
        existingFaculty.setName(faculty.getName());

        Faculty savedFaculty = facultyRepository.save(existingFaculty);
        return ResponseEntity.ok(savedFaculty);
    }

    @DeleteMapping("/faculties/{code}")
    public ResponseEntity<?> deleteFaculty(@PathVariable String code) {
        if (!facultyRepository.existsById(code)) {
            return ResponseEntity.notFound().build();
        }

        facultyRepository.deleteById(code);
        return ResponseEntity.ok().build();
    }

    // ==================== SEARCH ENDPOINTS ====================
    @GetMapping("/lecturers/search")
    public ResponseEntity<Page<Lecturer>> searchLecturers(
            @RequestParam String searchPhrase,
            Pageable pageable) {

        Page<Lecturer> lecturers = lecturerRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchPhrase, pageable);
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/students/search")
    public ResponseEntity<Page<Student>> searchStudents(
            @RequestParam String searchPhrase,
            Pageable pageable) {

        Page<Student> students = studentRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchPhrase, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/faculties/search")
    public ResponseEntity<Page<Faculty>> searchFaculties(
            @RequestParam String searchPhrase,
            Pageable pageable) {

        Page<Faculty> faculties = facultyRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                searchPhrase, pageable);
        return ResponseEntity.ok(faculties);
    }
}