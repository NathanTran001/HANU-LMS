package fit.se2.hanulms.model;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @ManyToOne
    private Faculty faculty;
    @ManyToMany
    private List<Course> courses;
    private String username;

    private String password;
    private String role;
    public Lecturer() {}
    public Lecturer(UserTemplate userTemplate, PasswordEncoder passwordEncoder) {
        this.name = userTemplate.getName();
        this.email = userTemplate.getEmail();
        this.faculty = userTemplate.getFaculty();
        this.username = userTemplate.getUsername();
        this.password = passwordEncoder.encode(userTemplate.getPassword()); // Encode pw
        this.role = "LECTURER";
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
