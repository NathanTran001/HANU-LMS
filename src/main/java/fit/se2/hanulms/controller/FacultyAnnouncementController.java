package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.Faculty;
import fit.se2.hanulms.model.FacultyAnnouncement;
import fit.se2.hanulms.model.Lecturer;
import fit.se2.hanulms.model.Student;
import fit.se2.hanulms.repository.FacultyAnnouncementRepository;
import fit.se2.hanulms.repository.FacultyRepository;
import fit.se2.hanulms.repository.LecturerRepository;
import fit.se2.hanulms.repository.StudentRepository;
import fit.se2.hanulms.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/faculty-announcements")
public class FacultyAnnouncementController {

    @Autowired
    private FacultyAnnouncementRepository facultyAnnouncementRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Get all faculty announcements for user's faculty
    @GetMapping
    public ResponseEntity<?> getFacultyAnnouncements(HttpServletRequest request) {
        try {
            String username = jwtUtil.extractUsername(jwtUtil.extractTokenFromCookie(request));
            if (username == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("Unable to extract username from request"));
            }

            Optional<Lecturer> lectOpt = lecturerRepository.findByUsername(username);
            Optional<Student> stuOpt = studentRepository.findByUsername(username);
            Faculty faculty = null;

            if (lectOpt.isPresent()) {
                Lecturer lecturer = lectOpt.get();
                faculty = lecturer.getFaculty();

            }
            if (stuOpt.isPresent()) {
                Student student = stuOpt.get();
                faculty = student.getFaculty();
            }

            if (faculty == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("User is not associated with any faculty"));
            }

            List<FacultyAnnouncement> announcements = faculty.getFacultyAnnouncements();
            return ResponseEntity.ok(announcements);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error retrieving faculty announcements: " + e.getMessage()));
        }
    }

    // Create faculty announcement
    @PostMapping
    public ResponseEntity<?> createFacultyAnnouncement(
            @RequestBody @Valid FacultyAnnouncement facultyAnnouncementDTO,
            HttpServletRequest request) {
        try {
            String username = jwtUtil.extractUsername(jwtUtil.extractTokenFromCookie(request));
            if (username == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("Unable to extract username from request"));
            }

            Optional<Lecturer> lectOpt = lecturerRepository.findByUsername(username);
            if (lectOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("User not found with username: " + username));
            }

            Lecturer lecturer = lectOpt.get();
            Faculty faculty = lecturer.getFaculty();
            if (faculty == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("User is not associated with any faculty"));
            }

            // Create faculty announcement entity from DTO
            FacultyAnnouncement facultyAnnouncement = new FacultyAnnouncement();
            facultyAnnouncement.setTitle(facultyAnnouncementDTO.getTitle());
            facultyAnnouncement.setDescription(facultyAnnouncementDTO.getDescription());
            facultyAnnouncement.setFaculty(faculty);

            FacultyAnnouncement savedAnnouncement = facultyAnnouncementRepository.save(facultyAnnouncement);
            return ResponseEntity.ok(savedAnnouncement);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error creating faculty announcement: " + e.getMessage()));
        }
    }

    // Update faculty announcement
    @PutMapping("/{announcementId}")
    public ResponseEntity<?> updateFacultyAnnouncement(
            @PathVariable Long announcementId,
            @RequestBody @Valid FacultyAnnouncement facultyAnnouncementDTO,
            HttpServletRequest request) {
        try {
            String username = jwtUtil.extractUsername(jwtUtil.extractTokenFromCookie(request));
            if (username == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("Unable to extract username from request"));
            }

            Optional<Lecturer> lectOpt = lecturerRepository.findByUsername(username);
            if (lectOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("User not found with username: " + username));
            }

            Lecturer lecturer = lectOpt.get();
            Faculty faculty = lecturer.getFaculty();
            if (faculty == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("User is not associated with any faculty"));
            }

            Optional<FacultyAnnouncement> announcementOpt = facultyAnnouncementRepository.findById(announcementId);
            if (announcementOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("Faculty announcement not found with ID: " + announcementId));
            }

            FacultyAnnouncement announcement = announcementOpt.get();

            // Check if the announcement belongs to the user's faculty
            if (!announcement.getFaculty().getCode().equals(faculty.getCode())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(List.of("You can only update announcements from your own faculty"));
            }

            // Update the announcement
            announcement.setTitle(facultyAnnouncementDTO.getTitle());
            announcement.setDescription(facultyAnnouncementDTO.getDescription());

            FacultyAnnouncement updatedAnnouncement = facultyAnnouncementRepository.save(announcement);
            return ResponseEntity.ok(updatedAnnouncement);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error updating faculty announcement: " + e.getMessage()));
        }
    }

    // Delete faculty announcement
    @DeleteMapping("/{announcementId}")
    public ResponseEntity<?> deleteFacultyAnnouncement(
            @PathVariable Long announcementId,
            HttpServletRequest request) {
        try {
            String username = jwtUtil.extractUsername(jwtUtil.extractTokenFromCookie(request));
            if (username == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("Unable to extract username from request"));
            }

            Optional<Lecturer> lectOpt = lecturerRepository.findByUsername(username);
            if (lectOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("User not found with username: " + username));
            }

            Lecturer lecturer = lectOpt.get();
            Faculty faculty = lecturer.getFaculty();
            if (faculty == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("User is not associated with any faculty"));
            }

            Optional<FacultyAnnouncement> announcementOpt = facultyAnnouncementRepository.findById(announcementId);
            if (announcementOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("Faculty announcement not found with ID: " + announcementId));
            }

            FacultyAnnouncement announcement = announcementOpt.get();

            // Check if the announcement belongs to the user's faculty
            if (!announcement.getFaculty().getCode().equals(faculty.getCode())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(List.of("You can only delete announcements from your own faculty"));
            }

            facultyAnnouncementRepository.deleteById(announcementId);
            return ResponseEntity.ok("Faculty announcement deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error deleting faculty announcement: " + e.getMessage()));
        }
    }
}