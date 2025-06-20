package fit.se2.hanulms.controller;

import fit.se2.hanulms.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = minioService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/minio-test")
    public ResponseEntity<String> testMinio() {
        try {
            minioService.ensureBucketExists();
            return ResponseEntity.ok("MinIO connection successful! Bucket is ready.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("MinIO connection failed: " + e.getMessage());
        }
    }
}