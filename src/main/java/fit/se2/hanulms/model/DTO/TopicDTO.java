package fit.se2.hanulms.model.DTO;

import fit.se2.hanulms.model.Topic;
import fit.se2.hanulms.model.TopicItem;

import java.util.List;

public class TopicDTO {
    private Long id;
    private String title;
    private List<TopicItem> topicItems;

    public TopicDTO() {
    }

    public TopicDTO(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.topicItems = topic.getTopicItems();
    }

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

    public List<TopicItem> getTopicItems() {
        return topicItems;
    }

    public void setTopicItems(List<TopicItem> topicItems) {
        this.topicItems = topicItems;
    }
}
