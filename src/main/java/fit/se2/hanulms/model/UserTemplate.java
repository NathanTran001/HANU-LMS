package fit.se2.hanulms.model;


import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

// When you receive a form, you receive UserTemplate object instead of User object. All validation will be done inside
// UserTemplate. If the UserTemplate object is valid, then values will be transferred to User
public class UserTemplate {
    @Length(min = 6, max = 60)
    private String username;
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[A-Z]).{6,60}$",
            message = "6 chars min (at least 1 digit & 1 uppercase letter)"
    )
    private String password;
    private String name;
    private String email;
    private Faculty faculty;
    private Set<Role> roles;

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
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}