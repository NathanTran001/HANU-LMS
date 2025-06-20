package fit.se2.hanulms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TopicItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 40, message = "Max title length is 40 characters!")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicItemType type;

    @ManyToOne
    @JsonIgnore
    private Topic topic;

    // For URL type items
    private String url;

    // For FILE and FOLDER type items - MinIO related fields
    private String minioObjectName;

    private String originalFilename;

    private Long fileSize;

    private String contentType;

    // For ordering items within a topic
    private Integer displayOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public TopicItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Factory methods for different types
    public static TopicItem createUrlItem(String title, String url, Topic topic) {
        TopicItem item = new TopicItem();
        item.setTitle(title);
        item.setType(TopicItemType.URL);
        item.setUrl(url);
        item.setTopic(topic);
        return item;
    }

    public static TopicItem createFileItem(String title, String minioObjectName,
                                           String originalFilename, String contentType,
                                           Long fileSize, Topic topic, TopicItemType type) {
        TopicItem item = new TopicItem();
        item.setTitle(title);
        item.setType(type);
        item.setMinioObjectName(minioObjectName);
        item.setOriginalFilename(originalFilename);
        item.setContentType(contentType);
        item.setFileSize(fileSize);
        item.setTopic(topic);
        return item;
    }

    // Helper methods
    public boolean isFileType() {
        return type == TopicItemType.FILE || type == TopicItemType.FOLDER;
    }

    public boolean isUrlType() {
        return type == TopicItemType.URL;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TopicItemType getType() {
        return type;
    }

    public void setType(TopicItemType type) {
        this.type = type;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMinioObjectName() {
        return minioObjectName;
    }

    public void setMinioObjectName(String minioObjectName) {
        this.minioObjectName = minioObjectName;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
