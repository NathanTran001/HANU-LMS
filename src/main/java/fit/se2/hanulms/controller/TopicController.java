package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.Course;
import fit.se2.hanulms.model.DTO.TopicDTO;
import fit.se2.hanulms.model.Topic;
import fit.se2.hanulms.repository.*;
import fit.se2.hanulms.util.Error;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topic")
public class TopicController {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    Error error;

    // CREATE
    @PostMapping("/{courseCode}")
    public ResponseEntity<?> createTopic(@PathVariable String courseCode,
                                         @Valid @RequestBody Topic topic,
                                         BindingResult result) {
        if (!result.getFieldErrors("title").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getErrorMessages(result));
        }
        if (courseCode == null || courseCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of("Invalid course code!"));
        } else {
            if (!courseRepository.existsById(courseCode)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(List.of("No course found with course code: " + courseCode));
            }
        }
        Course course = courseRepository.getReferenceById(courseCode);
        List<Topic> topics = course.getTopics();
        topics.add(topic);
        course.setTopics(topics);
        topic.setCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TopicDTO(topicRepository.save(topic)));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        return ResponseEntity.ok(topicRepository.findAll().stream().map(TopicDTO::new).collect(Collectors.toList()));
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getTopic(@PathVariable Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        return ResponseEntity.ok(new TopicDTO(topic));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable Long id,
                                         @Valid @RequestBody Topic updatedTopic,
                                         BindingResult result) {
        if (!result.getFieldErrors("title").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getErrorMessages(result));
        }

        Optional<Topic> topicOptional = topicRepository.findById(id);
        if (topicOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of("No topic found with id: " + id));
        }

        Topic topic = topicOptional.get();
        topic.setTitle(updatedTopic.getTitle());

        Topic savedTopic = topicRepository.save(topic);
        return ResponseEntity.ok(new TopicDTO(savedTopic));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        if (!topicRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of("No topic found with id: " + id));
        }
        topicRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
