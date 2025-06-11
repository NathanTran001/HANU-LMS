package fit.se2.hanulms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("STUDENT")
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class Student extends LMSUser {

    private String name;
    private String email;

    @ManyToOne
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"students"})
    private Faculty faculty;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"students"})
    private List<Course> courses;

    public Student() {
        super();
    }

    public Student(UserTemplate userTemplate, PasswordEncoder passwordEncoder) {
        super(userTemplate, passwordEncoder, Set.of(Role.STUDENT));

        this.name = userTemplate.getName();
        this.email = userTemplate.getEmail();
        this.faculty = userTemplate.getFaculty();
    }

    // Getters and Setters for student-specific attributes only
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}