package fit.se2.hanulms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Please enter the title of topic")
    @Length(max = 20, message = "Max title length is 20 characters!")
    private String title;
    @ManyToOne
    private Course course;
    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    @OrderBy("displayOrder ASC")
    private List<TopicItem> topicItems = new ArrayList<>();

    public Topic() {
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<TopicItem> getTopicItems() {
        return topicItems;
    }

    public void setTopicItems(List<TopicItem> topicItems) {
        this.topicItems = topicItems;
    }
}
