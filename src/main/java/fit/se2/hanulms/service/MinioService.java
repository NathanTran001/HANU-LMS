package fit.se2.hanulms.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url-expiry-hours:24}")
    private int urlExpiryHours;

    /**
     * Check if bucket exists, create if not
     */
    public void ensureBucketExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                System.out.println("Bucket '" + bucketName + "' created successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error checking/creating bucket: " + e.getMessage());
            throw new RuntimeException("Failed to ensure bucket exists", e);
        }
    }

    /**
     * Upload file to MinIO and return MinIO object name
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    /**
     * Upload file to MinIO with optional custom prefix
     */
    public String uploadFile(MultipartFile file, String prefix) {
        try {
            ensureBucketExists();

            // Generate unique filename with optional prefix
            String fileName = generateUniqueFileName(file.getOriginalFilename(), prefix);

            // Upload file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            System.out.println("File uploaded successfully: " + fileName);
            return fileName;

        } catch (Exception e) {
            System.err.println("Error uploading file: " + e.getMessage());
            throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Get presigned URL for file access
     */
    public String getFileUrl(String minioObjectName) {
        if (minioObjectName == null || minioObjectName.trim().isEmpty()) {
            return null;
        }

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(minioObjectName)
                            .expiry(urlExpiryHours * 60 * 60) // Convert hours to seconds
                            .build()
            );
        } catch (Exception e) {
            System.err.println("Error getting file URL for: " + minioObjectName + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete file from MinIO
     */
    public boolean deleteFile(String minioObjectName) {
        if (minioObjectName == null || minioObjectName.trim().isEmpty()) {
            return false;
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minioObjectName)
                            .build()
            );
            System.out.println("File deleted successfully: " + minioObjectName);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting file: " + minioObjectName + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if file exists in MinIO
     */
    public boolean fileExists(String minioObjectName) {
        if (minioObjectName == null || minioObjectName.trim().isEmpty()) {
            return false;
        }

        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minioObjectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get file metadata
     */
    public ObjectStat getFileMetadata(String minioObjectName) {
        try {
            StatObjectResponse response = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minioObjectName)
                            .build()
            );

            return new ObjectStat(
                    response.object(),
                    response.size(),
                    response.contentType(),
                    response.lastModified()
            );
        } catch (Exception e) {
            System.err.println("Error getting file metadata: " + minioObjectName + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Generate unique filename
     */
    private String generateUniqueFileName(String originalFilename, String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        String fileName = timestamp + "_" + uuid + "_" + originalFilename;

        if (prefix != null && !prefix.trim().isEmpty()) {
            fileName = prefix + "/" + fileName;
        }

        return fileName;
    }

    // Inner class for file metadata
    public static class ObjectStat {
        private final String objectName;
        private final long size;
        private final String contentType;
        private final java.time.ZonedDateTime lastModified;

        public ObjectStat(String objectName, long size, String contentType, java.time.ZonedDateTime lastModified) {
            this.objectName = objectName;
            this.size = size;
            this.contentType = contentType;
            this.lastModified = lastModified;
        }

        // Getters
        public String getObjectName() {
            return objectName;
        }

        public long getSize() {
            return size;
        }

        public String getContentType() {
            return contentType;
        }

        public java.time.ZonedDateTime getLastModified() {
            return lastModified;
        }
    }
}