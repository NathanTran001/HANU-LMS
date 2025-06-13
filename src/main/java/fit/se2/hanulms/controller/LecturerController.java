package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.*;
import fit.se2.hanulms.model.*;
import fit.se2.hanulms.model.DTO.FacultyDTO;
import fit.se2.hanulms.model.DTO.LecturerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    StudentRepository studentRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    AssignmentRepository assignmentRepository;

    // ============== LECTURER ENDPOINTS ==============
    @GetMapping("/lecturers/all")
    public ResponseEntity<List<LecturerDTO>> listAllLecturers(@RequestParam(required = false) String code) {
        // Getting all lecturers in a specific faculty, if no faculty code provided, find all lecturers
        List<Lecturer> lecturers = lecturerRepository.findAll();
        if (code != null && !code.isEmpty()) {
            lecturers = lecturers.stream()
                    .filter(lecturer -> lecturer.getFaculty() != null && lecturer.getFaculty().getCode().equals(code))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(
                    lecturers.stream().map(LecturerDTO::new).collect(Collectors.toList())
            );
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/faculties/all")
    public ResponseEntity<List<FacultyDTO>> listFacultyAll() {
        return ResponseEntity.ok(facultyRepository.findAll().stream().map(
                FacultyDTO::new
        ).collect(Collectors.toList()));
    }

    // ============== ENROLLMENT ENDPOINT ==============

//    @PostMapping("/courses/{courseCode}/enroll")
//    public ResponseEntity<?> enrollStudent(@PathVariable String courseCode,
//                                           @RequestBody Map<String, Object> request) {
//        try {
//            Long studentId = Long.valueOf(request.get("studentId").toString());
//            String enrolmentKey = (String) request.get("enrolmentKey");
//
//            Optional<Course> optionalCourse = courseRepository.findById(courseCode);
//            if (!optionalCourse.isPresent()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            Optional<Student> optionalStudent = studentRepository.findById(studentId);
//            if (!optionalStudent.isPresent()) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "Student not found"));
//            }
//
//            Course course = optionalCourse.get();
//            Student student = optionalStudent.get();
//
//            if (!course.getEnrolmentKey().equals(enrolmentKey)) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "Invalid enrollment key"));
//            }
//
//            if (course.getLecturers().contains(student)) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "Student already enrolled"));
//            }
//
//            student.getCourses().add(course);
//            course.getStudents().add(student);
//
//            studentRepository.save(student);
//            courseRepository.save(course);
//
//            return ResponseEntity.ok(Map.of("message", "Successfully enrolled"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Failed to enroll student"));
//        }
//    }

}