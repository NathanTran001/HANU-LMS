package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Topic;
import fit.se2.hanulms.model.TopicItem;
import fit.se2.hanulms.model.TopicItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicItemRepository extends JpaRepository<TopicItem, Long> {

    /**
     * Find all topic items by topic, ordered by display order
     */
    List<TopicItem> findByTopicOrderByDisplayOrderAsc(Topic topic);

    /**
     * Find topic items by topic and type
     */
    List<TopicItem> findByTopicAndType(Topic topic, TopicItemType type);

    /**
     * Find topic items by topic and IDs (for reordering)
     */
    List<TopicItem> findByTopicAndIdIn(Topic topic, List<Long> ids);

    /**
     * Get maximum display order for a topic
     */
    @Query("SELECT MAX(ti.displayOrder) FROM TopicItem ti WHERE ti.topic = :topic")
    Integer findMaxDisplayOrderByTopic(@Param("topic") Topic topic);

    /**
     * Find all file-type items that have MinIO object names
     */
    @Query("SELECT ti FROM TopicItem ti WHERE ti.type IN ('FILE', 'FOLDER') AND ti.minioObjectName IS NOT NULL")
    List<TopicItem> findAllFileTypeItems();

    /**
     * Find topic items by MinIO object name (useful for cleanup operations)
     */
    TopicItem findByMinioObjectName(String minioObjectName);

    /**
     * Count topic items by topic
     */
    long countByTopic(Topic topic);

    /**
     * Find topic items by topic and title (for duplicate checking)
     */
    List<TopicItem> findByTopicAndTitle(Topic topic, String title);

    /**
     * Delete all topic items by topic (cascade should handle this, but useful for explicit cleanup)
     */
    void deleteByTopic(Topic topic);
}