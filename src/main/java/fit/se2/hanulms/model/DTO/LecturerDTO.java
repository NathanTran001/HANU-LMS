package fit.se2.hanulms.model.DTO;

import fit.se2.hanulms.model.Course;
import fit.se2.hanulms.model.Lecturer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LecturerDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String facultyCode;
    private String facultyName;
    private List<Map<String, String>> courses;

    public LecturerDTO() {
    }

    public LecturerDTO(Lecturer lecturer) {
        this.id = lecturer.getId();
        this.username = lecturer.getUsername();
        this.name = lecturer.getName();
        this.email = lecturer.getEmail();
        this.facultyCode = lecturer.getFaculty().getCode();
        this.facultyName = lecturer.getFaculty().getName();

        // Handle courses - create list of maps
        if (lecturer.getCourses() != null && !lecturer.getCourses().isEmpty()) {
            this.courses = lecturer.getCourses().stream()
                    .map(courseEntity -> {
                        Map<String, String> courseMap = new HashMap<>();
                        courseMap.put("code", courseEntity.getCode());
                        courseMap.put("name", courseEntity.getName());
                        return courseMap;
                    })
                    .collect(Collectors.toList());
        } else {
            this.courses = new ArrayList<>();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public List<Map<String, String>> getCourses() {
        return courses;
    }

    public void setCourses(List<Map<String, String>> courses) {
        this.courses = courses;
    }
}