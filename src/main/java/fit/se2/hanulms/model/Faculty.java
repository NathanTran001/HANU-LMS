package fit.se2.hanulms.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "code")
public class Faculty {
    @Id
    private String code;
    private String name;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"faculty"})
    private List<Lecturer> lecturers;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"faculty"})
    private List<Student> students;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"faculty"})
    private List<Course> courses;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    private List<FacultyAnnouncement> facultyAnnouncements;

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

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<FacultyAnnouncement> getFacultyAnnouncements() {
        return facultyAnnouncements;
    }

    public void setFacultyAnnouncements(List<FacultyAnnouncement> facultyAnnouncements) {
        this.facultyAnnouncements = facultyAnnouncements;
    }
}
