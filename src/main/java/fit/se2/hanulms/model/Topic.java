package fit.se2.hanulms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "Please enter the title of topic")
    @Length(max = 20, message = "Max title length is 20 characters!")
    private String title;
    @NotEmpty(message = "Please enter the description of topic")
    @Length(max = 40, message = "Max description length is 40 characters!")
    private String description;
    @ManyToOne
    private Course course;
    //    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
//    private List<TopicItem> topicItems;
    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<File> file;
    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<Assignment> assignments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<File> getFile() {
        return file;
    }

    public void setFile(List<File> file) {
        this.file = file;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

}
