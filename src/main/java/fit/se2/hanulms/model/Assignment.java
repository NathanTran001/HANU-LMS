package fit.se2.hanulms.model;

import jakarta.persistence.*;

@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assignmentId;

    private String assTitle;

    private String assDescription;

    private String attachment;

    @ManyToOne
    private Topic topic;

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssTitle() {
        return assTitle;
    }

    public void setAssTitle(String assTitle) {
        this.assTitle = assTitle;
    }

    public String getAssDescription() {
        return assDescription;
    }

    public void setAssDescription(String assDescription) {
        this.assDescription = assDescription;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
