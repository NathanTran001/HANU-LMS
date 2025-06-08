package fit.se2.hanulms.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "lecturer")
@PrimaryKeyJoinColumn(name = "user_id")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@DiscriminatorValue("LECTURER")
public class Lecturer extends LMSUser {

    private String name;
    private String email;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "lecturer_courses",
            joinColumns = @JoinColumn(name = "lecturer_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    public Lecturer() {
        super();
    }

    public Lecturer(UserTemplate userTemplate, PasswordEncoder passwordEncoder) {
        super(userTemplate,
                passwordEncoder,
                Set.of(Role.LECTURER));

        this.name = userTemplate.getName();
        this.email = userTemplate.getEmail();
        this.faculty = userTemplate.getFaculty();
    }

    // Getters and Setters for lecturer-specific attributes only
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