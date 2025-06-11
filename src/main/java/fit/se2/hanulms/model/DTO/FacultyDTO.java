package fit.se2.hanulms.model.DTO;

import fit.se2.hanulms.model.Faculty;

public class FacultyDTO {
    private String code;
    private String name;
    private Integer lecturerCount;
    private Integer studentCount;
    private Integer courseCount;

    public FacultyDTO() {
    }

    public FacultyDTO(Faculty faculty) {
        this.code = faculty.getCode();
        this.name = faculty.getName();
        this.lecturerCount = faculty.getLecturers() != null ? faculty.getLecturers().size() : 0;
        this.studentCount = faculty.getStudents() != null ? faculty.getStudents().size() : 0;
        this.courseCount = faculty.getCourses() != null ? faculty.getCourses().size() : 0;
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

    public Integer getLecturerCount() {
        return lecturerCount;
    }

    public void setLecturerCount(Integer lecturerCount) {
        this.lecturerCount = lecturerCount;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }
}
