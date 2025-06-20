package fit.se2.hanulms.controller;

import fit.se2.hanulms.repository.*;
import fit.se2.hanulms.model.*;
import fit.se2.hanulms.model.DTO.FacultyDTO;
import fit.se2.hanulms.model.DTO.LecturerDTO;
import fit.se2.hanulms.model.DTO.StudentDTO;
import fit.se2.hanulms.util.Error;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
    @Autowired
    private Error error;

    // ==================== LECTURERS ====================
    @GetMapping("/lecturers")
    public ResponseEntity<Page<LecturerDTO>> listLecturer(Pageable pageable) {
        Page<Lecturer> lecturers = lecturerRepository.findAll(pageable);
        return ResponseEntity.ok(lecturers.map(LecturerDTO::new));
    }

    @GetMapping("/lecturers/{id}")
    public ResponseEntity<?> getLecturer(@PathVariable Long id) {
        Optional<Lecturer> existing = lecturerRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Lecturer not found"));
        }

        return ResponseEntity.ok(new LecturerDTO(existing.get()));
    }

    @PostMapping("/lecturers")
    public ResponseEntity<?> createLecturer(@Valid @RequestBody UserTemplate userTemplate, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error.getErrorMessages(result));
        }

        // Check username uniqueness across all users (lecturers, students, admins)
        if (lecturerRepository.existsByUsername(userTemplate.getUsername()) ||
                studentRepository.existsByUsername(userTemplate.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("Username already exists"));
        }

        String facultyCode = userTemplate.getFacultyCode();
        if (!facultyRepository.existsById(facultyCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("No faculty found with faculty code " + facultyCode));
        }

        Lecturer newLecturer = new Lecturer(userTemplate, p, facultyRepository.getReferenceById(facultyCode));
        lecturerRepository.save(newLecturer);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LecturerDTO(newLecturer));
    }

    @PutMapping("/lecturers/{id}")
    public ResponseEntity<?> editLecturer(@PathVariable Long id,
                                          @Valid
                                          @RequestBody UserTemplate userTemplate,
                                          BindingResult result) {
        boolean hasNonPasswordErrors = false;
        if (userTemplate.getPassword() == null || userTemplate.getPassword().trim().isEmpty()) {
            hasNonPasswordErrors = result.getFieldErrors().stream()
                    .anyMatch(error -> !"password".equals(error.getField()));
            hasNonPasswordErrors = hasNonPasswordErrors || result.getGlobalErrors().size() > 0;
        } else {
            hasNonPasswordErrors = result.hasErrors();
        }

        if (hasNonPasswordErrors) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error.getErrorMessages(result));
        }

        Optional<Lecturer> chosenLecturer = lecturerRepository.findById(id);
        Optional<Faculty> chosenFaculty = facultyRepository.findById(userTemplate.getFacultyCode());
        if (chosenLecturer.isEmpty() || chosenFaculty.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(List.of("Lecturer or Faculty not found!"));
        }
        Lecturer lecturer = chosenLecturer.get();
        lecturer.setName(userTemplate.getName());
        lecturer.setEmail(userTemplate.getEmail());

        // Only update password if provided
        if (userTemplate.getPassword() != null && !userTemplate.getPassword().isEmpty()) {
            lecturer.setPassword(p.encode(userTemplate.getPassword()));
        }

        lecturer.setFaculty(chosenFaculty.get());
        lecturerRepository.save(lecturer);
        return ResponseEntity.ok(new LecturerDTO(lecturer));
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
    public ResponseEntity<Page<StudentDTO>> listStudent(Pageable pageable) {
        Page<Student> students = studentRepository.findAll(pageable);
        return ResponseEntity.ok(students.map(StudentDTO::new));
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
    public ResponseEntity<?> createStudent(@Valid @RequestBody UserTemplate userTemplate, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error.getErrorMessages(result));
        }

        // Check username uniqueness across all users
        if (lecturerRepository.existsByUsername(userTemplate.getUsername()) ||
                studentRepository.existsByUsername(userTemplate.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("Username already exists"));
        }

        String facultyCode = userTemplate.getFacultyCode();
        if (!facultyRepository.existsById(facultyCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("No faculty found with faculty code " + facultyCode));
        }

        Student newStudent = new Student(userTemplate, p, facultyRepository.getReferenceById(facultyCode));
        studentRepository.save(newStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StudentDTO(newStudent));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> editStudent(@PathVariable Long id,
                                         @Valid
                                         @RequestBody UserTemplate userTemplate,
                                         BindingResult result) {
        boolean hasNonPasswordErrors = false;
        if (userTemplate.getPassword() == null || userTemplate.getPassword().trim().isEmpty()) {
            hasNonPasswordErrors = result.getFieldErrors().stream()
                    .anyMatch(error -> !"password".equals(error.getField()));
            hasNonPasswordErrors = hasNonPasswordErrors || result.getGlobalErrors().size() > 0;
        } else {
            hasNonPasswordErrors = result.hasErrors();
        }

        if (hasNonPasswordErrors) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error.getErrorMessages(result));
        }

        Optional<Student> selectedStudent = studentRepository.findById(id);
        Optional<Faculty> chosenFaculty = facultyRepository.findById(userTemplate.getFacultyCode());
        if (selectedStudent.isEmpty() || chosenFaculty.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(List.of("Student or Faculty not found!"));
        }

        Student student = selectedStudent.get();
        student.setName(userTemplate.getName());
        student.setEmail(userTemplate.getEmail());

        // Only update password if provided
        if (userTemplate.getPassword() != null && !userTemplate.getPassword().isEmpty()) {
            student.setPassword(p.encode(userTemplate.getPassword()));
        }

        student.setFaculty(chosenFaculty.get());
        studentRepository.save(student);
        return ResponseEntity.ok(new StudentDTO(student));
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
    public ResponseEntity<Page<FacultyDTO>> listFaculty(Pageable pageable) {
        Page<Faculty> facultyPage = facultyRepository.findAll(pageable);
        return ResponseEntity.ok(facultyPage.map(FacultyDTO::new));
//        Page<FacultyDTO> facultyDTOPage = facultyService.getFacultiesWithCounts(pageable);
//        return ResponseEntity.ok(facultyDTOPage);

    }

    @GetMapping("/faculties/{code}")
    public ResponseEntity<?> getFaculty(@PathVariable String code) {
        Optional<Faculty> existing = facultyRepository.findById(code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Faculty not found"));
        }

        return ResponseEntity.ok((new FacultyDTO(existing.get())));
    }

    @PostMapping("/faculties")
    public ResponseEntity<?> createFaculty(@Valid @RequestBody Faculty faculty) {
        if (facultyRepository.existsById(faculty.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("Faculty code already exists"));
        }
        Faculty saved = facultyRepository.save(faculty);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FacultyDTO(saved));
    }

    @PutMapping("/faculties/{code}")
    public ResponseEntity<?> editFaculty(@PathVariable String code, @Valid @RequestBody Faculty faculty) {
        Optional<Faculty> existing = facultyRepository.findById(code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(List.of("Faculty not found"));
        }

        Faculty existingFaculty = existing.get();
        existingFaculty.setName(faculty.getName());

        Faculty savedFaculty = facultyRepository.save(existingFaculty);
        return ResponseEntity.ok(new FacultyDTO(savedFaculty));
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
    public ResponseEntity<Page<LecturerDTO>> searchLecturers(
            @RequestParam String searchPhrase,
            Pageable pageable) {

        Page<Lecturer> lecturers = lecturerRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchPhrase, pageable);
        return ResponseEntity.ok(lecturers.map(LecturerDTO::new));
    }

    @GetMapping("/students/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam String searchPhrase,
            Pageable pageable) {

        Page<Student> students = studentRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchPhrase, pageable);
        return ResponseEntity.ok(students.map(StudentDTO::new));
    }

    @GetMapping("/faculties/search")
    public ResponseEntity<Page<FacultyDTO>> searchFaculties(
            @RequestParam String searchPhrase,
            Pageable pageable) {

        Page<Faculty> faculties = facultyRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                searchPhrase, pageable);
        return ResponseEntity.ok(faculties.map(FacultyDTO::new));
    }
}