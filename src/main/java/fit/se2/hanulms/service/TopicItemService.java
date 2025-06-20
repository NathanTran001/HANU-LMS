package fit.se2.hanulms.service;

import fit.se2.hanulms.model.Topic;
import fit.se2.hanulms.model.TopicItem;
import fit.se2.hanulms.model.TopicItemType;
import fit.se2.hanulms.repository.TopicItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TopicItemService {

    @Autowired
    private TopicItemRepository topicItemRepository;

    @Autowired
    private MinioService minioService;

    /**
     * Create a URL-type topic item
     */
    public TopicItem createUrlItem(String title, String url, Topic topic) {
        TopicItem item = TopicItem.createUrlItem(title, url, topic);
        item.setDisplayOrder(getNextDisplayOrder(topic));
        return topicItemRepository.save(item);
    }

    /**
     * Create a FILE-type topic item
     */
    public TopicItem createFileItem(String title, MultipartFile file, Topic topic) {
        return createFileItem(title, file, topic, TopicItemType.FILE);
    }

    /**
     * Create a FOLDER-type topic item (ZIP file)
     */
    public TopicItem createFolderItem(String title, MultipartFile file, Topic topic) {
        return createFileItem(title, file, topic, TopicItemType.FOLDER);
    }

    /**
     * Private method to handle file upload and create topic item
     */
    private TopicItem createFileItem(String title, MultipartFile file, Topic topic, TopicItemType type) {
        try {
            // Upload file to MinIO
            String prefix = "topic_" + topic.getId();
            String minioObjectName = minioService.uploadFile(file, prefix);

            // Create topic item
            TopicItem item = TopicItem.createFileItem(
                    title,
                    minioObjectName,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    topic,
                    type
            );

            item.setDisplayOrder(getNextDisplayOrder(topic));
            return topicItemRepository.save(item);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create file item: " + e.getMessage(), e);
        }
    }

    /**
     * Get topic item by ID
     */
    public Optional<TopicItem> getTopicItem(Long id) {
        return topicItemRepository.findById(id);
    }

    /**
     * Get all topic items for a topic
     */
    public List<TopicItem> getTopicItemsByTopic(Topic topic) {
        return topicItemRepository.findByTopicOrderByDisplayOrderAsc(topic);
    }

    /**
     * Update topic item
     */
    public TopicItem updateTopicItem(Long id, String title, String url) {
        TopicItem item = topicItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicItem not found"));

        item.setTitle(title);
        if (item.isUrlType()) {
            item.setUrl(url);
        }

        return topicItemRepository.save(item);
    }

    /**
     * Delete topic item
     */
    public void deleteTopicItem(Long id) {
        TopicItem item = topicItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicItem not found"));

        // If it's a file type, delete from MinIO
        if (item.isFileType() && item.getMinioObjectName() != null) {
            minioService.deleteFile(item.getMinioObjectName());
        }

        topicItemRepository.delete(item);
    }

    /**
     * Get file download URL for file-type items
     */
    public String getFileDownloadUrl(Long id) {
        TopicItem item = topicItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicItem not found"));

        if (!item.isFileType() || item.getMinioObjectName() == null) {
            throw new RuntimeException("TopicItem is not a file type or has no associated file");
        }

        return minioService.getFileUrl(item.getMinioObjectName());
    }

    /**
     * Update display order of topic items
     */
    public void updateDisplayOrder(Long id, Integer newOrder) {
        TopicItem item = topicItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicItem not found"));

        item.setDisplayOrder(newOrder);
        topicItemRepository.save(item);
    }

    /**
     * Reorder topic items within a topic
     */
    public void reorderTopicItems(Topic topic, List<Long> orderedIds) {
        List<TopicItem> items = topicItemRepository.findByTopicAndIdIn(topic, orderedIds);

        for (int i = 0; i < orderedIds.size(); i++) {
            Long id = orderedIds.get(i);
            TopicItem item = items.stream()
                    .filter(ti -> ti.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("TopicItem not found: " + id));

            item.setDisplayOrder(i + 1);
        }

        topicItemRepository.saveAll(items);
    }

    /**
     * Get next display order for a topic
     */
    private Integer getNextDisplayOrder(Topic topic) {
        Integer maxOrder = topicItemRepository.findMaxDisplayOrderByTopic(topic);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }

    /**
     * Replace file for an existing file-type topic item
     */
    public TopicItem replaceFile(Long id, MultipartFile newFile) {
        TopicItem item = topicItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicItem not found"));

        if (!item.isFileType()) {
            throw new RuntimeException("TopicItem is not a file type");
        }

        try {
            // Delete old file from MinIO
            if (item.getMinioObjectName() != null) {
                minioService.deleteFile(item.getMinioObjectName());
            }

            // Upload new file
            String prefix = "topic_" + item.getTopic().getId();
            String minioObjectName = minioService.uploadFile(newFile, prefix);

            // Update item with new file info
            item.setMinioObjectName(minioObjectName);
            item.setOriginalFilename(newFile.getOriginalFilename());
            item.setContentType(newFile.getContentType());
            item.setFileSize(newFile.getSize());

            return topicItemRepository.save(item);

        } catch (Exception e) {
            throw new RuntimeException("Failed to replace file: " + e.getMessage(), e);
        }
    }
}