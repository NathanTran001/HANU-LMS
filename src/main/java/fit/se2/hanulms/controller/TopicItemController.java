package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.Topic;
import fit.se2.hanulms.model.TopicItem;
import fit.se2.hanulms.repository.CourseRepository;
import fit.se2.hanulms.repository.TopicRepository;
import fit.se2.hanulms.service.TopicItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topic-item")
public class TopicItemController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicItemService topicItemService;

    /**
     * Create a URL-type topic item
     */
    @PostMapping("/url")
    public ResponseEntity<?> createUrlItem(
            @RequestParam String title,
            @RequestParam String url,
            @RequestParam Long topicId) {
        try {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            TopicItem item = topicItemService.createUrlItem(title, url, topic);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(e.getMessage()));
        }
    }

    /**
     * Create a FILE-type topic item
     */
    @PostMapping("/file")
    public ResponseEntity<?> createFileItem(
            @RequestParam String title,
            @RequestParam MultipartFile file,
            @RequestParam Long topicId) {
        try {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            TopicItem item = topicItemService.createFileItem(title, file, topic);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(e.getMessage()));
        }
    }

    /**
     * Create a FOLDER-type topic item (ZIP file)
     */
    @PostMapping("/folder")
    public ResponseEntity<?> createFolderItem(
            @RequestParam String title,
            @RequestParam MultipartFile file,
            @RequestParam Long topicId) {
        try {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            TopicItem item = topicItemService.createFolderItem(title, file, topic);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(e.getMessage()));
        }
    }

    /**
     * Update topic item (title and URL for URL-type items)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopicItem(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String url) {
        try {
            TopicItem item = topicItemService.updateTopicItem(id, title, url);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(e.getMessage()));
        }
    }

    /**
     * Replace file for an existing file-type topic item
     */
    @PutMapping("/{id}/file")
    public ResponseEntity<?> replaceFile(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        try {
            TopicItem item = topicItemService.replaceFile(id, file);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(e.getMessage()));
        }
    }

    /**
     * Get file download URL for file-type topic items
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> getFileDownloadUrl(@PathVariable Long id) {
        try {
            String downloadUrl = topicItemService.getFileDownloadUrl(id);
            return ResponseEntity.ok(Map.of("downloadUrl", downloadUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete topic item
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopicItem(@PathVariable Long id) {
        try {
            topicItemService.deleteTopicItem(id);
            return ResponseEntity.ok(List.of("Topic item deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}