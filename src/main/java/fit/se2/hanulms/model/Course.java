package fit.se2.hanulms.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "code")
public class Course {
    @Id
    @NotEmpty(message = "Please insert course code")
    private String code;
    private String name;
    private String description;
    private String enrolmentKey;
    private String courseImage;
    @ManyToOne
    private Faculty faculty;
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.REMOVE)
    private List<Lecturer> lecturers;
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.REMOVE)
    private List<Student> students;
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Topic> topics;
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Announcement> announcements;

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnrolmentKey() {
        return enrolmentKey;
    }

    public void setEnrolmentKey(String enrolmentKey) {
        this.enrolmentKey = enrolmentKey;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public List<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(List<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}

