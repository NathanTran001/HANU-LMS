package fit.se2.hanulms.model.DTO;

import fit.se2.hanulms.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDTO {
    private String code;
    private String name;
    private String description;
    private String enrolmentKey;
    private String facultyCode;
    private String facultyName;
    private List<StudentDTO> students;
    private List<LecturerDTO> lecturers;

    public CourseDTO() {
    }

    public CourseDTO(Course course) {
        this.code = course.getCode();
        this.name = course.getName();
        this.description = course.getDescription();
        this.enrolmentKey = course.getEnrolmentKey();
        this.facultyCode = course.getFaculty().getCode();
        this.facultyName = course.getFaculty().getName();
        // Transform entities to DTOs
        this.students = course.getStudents() != null ?
                course.getStudents().stream()
                        .map(StudentDTO::new)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        this.lecturers = course.getLecturers() != null ?
                course.getLecturers().stream()
                        .map(LecturerDTO::new)
                        .collect(Collectors.toList()) :
                new ArrayList<>();
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

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }

    public List<LecturerDTO> getLecturers() {
        return lecturers;
    }

    public void setLecturers(List<LecturerDTO> lecturers) {
        this.lecturers = lecturers;
    }
}
