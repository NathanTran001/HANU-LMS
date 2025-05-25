package fit.se2.hanulms.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Faculty {
    @Id
    private String code;
    private String name;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    private List<Lecturer> lecturers;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    private List<Student> students;
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.REMOVE)
    private List<Course> courses;
    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
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

    public List<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(List<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
