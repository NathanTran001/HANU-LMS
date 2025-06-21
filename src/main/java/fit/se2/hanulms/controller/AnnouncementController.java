package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.Announcement;
import fit.se2.hanulms.model.Course;
import fit.se2.hanulms.repository.AnnouncementRepository;
import fit.se2.hanulms.repository.CourseRepository;
import fit.se2.hanulms.util.Error;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private Error error;

    @PostMapping("/course/{courseCode}")
    public ResponseEntity<?> createAnnouncement(
            @PathVariable String courseCode,
            @RequestBody @Valid Announcement announcementForm, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(List.of(error.getErrorMessages(result)));
            }

            Optional<Course> courseOpt = courseRepository.findById(courseCode);
            if (courseOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("Course not found with code: " + courseCode));
            }

            Course course = courseOpt.get();

            // Check if course already has an announcement (since it's OneToOne)
            if (course.getAnnouncement() != null) {
                return ResponseEntity.badRequest()
                        .body(List.of("Course already has an announcement. Use update instead."));
            }

            // Create announcement entity from DTO
            Announcement announcement = new Announcement();
            announcement.setCourse(course);
            announcement.setTitle(announcementForm.getTitle());
            announcement.setDescription(announcementForm.getDescription());

            Announcement savedAnnouncement = announcementRepository.save(announcement);
            return ResponseEntity.ok(savedAnnouncement);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error creating announcement: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<?> deleteAnnouncementById(@PathVariable Long announcementId) {
        try {
            Optional<Announcement> announcementOpt = announcementRepository.findById(announcementId);
            if (announcementOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("Announcement not found with ID: " + announcementId));
            }

            announcementRepository.deleteById(announcementId);
            return ResponseEntity.ok("Announcement deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error deleting announcement: " + e.getMessage()));
        }
    }

    @PutMapping("/{announcementId}")
    public ResponseEntity<?> updateAnnouncementById(
            @PathVariable Long announcementId,
            @RequestBody @Valid Announcement announcementForm, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(List.of(error.getErrorMessages(result)));
            }
            Optional<Announcement> announcementOpt = announcementRepository.findById(announcementId);
            if (announcementOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(List.of("Announcement not found with ID: " + announcementId));
            }

            Announcement announcement = announcementOpt.get();
            announcement.setTitle(announcementForm.getTitle());
            announcement.setDescription(announcementForm.getDescription());

            Announcement updatedAnnouncement = announcementRepository.save(announcement);
            return ResponseEntity.ok(updatedAnnouncement);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error updating announcement: " + e.getMessage()));
        }
    }
}