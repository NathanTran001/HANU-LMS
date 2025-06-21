package fit.se2.hanulms.controller;

import fit.se2.hanulms.repository.*;
import fit.se2.hanulms.model.*;
import fit.se2.hanulms.model.DTO.LecturerDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

}