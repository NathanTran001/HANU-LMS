package fit.se2.hanulms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int topicId;

    @NotEmpty(message = "Please enter the title of topic")
    @Length(max = 20, message = "The title should be less than 20")
    private String topicName;

    @NotEmpty(message = "Please enter the description of topic")
    @Length(max = 40, message = "The description should be less than 40")
    private String topicDescription;


    @ManyToOne
    private Course course;

    @OneToMany(mappedBy = "topic")
    private List<File> file;

    @OneToMany(mappedBy = "topic")
    private List<Assignment> assignments;


    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public List<File> getFile() {
        return file;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }


}
