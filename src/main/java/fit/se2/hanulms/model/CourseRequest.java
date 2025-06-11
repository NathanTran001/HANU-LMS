package fit.se2.hanulms.model;

import java.util.List;

public class CourseRequest {
    private String code;
    private String name;
    private String description;
    private String enrolmentKey;
    private String facultyCode;
    private List<Long> lecturerIds;

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

    public List<Long> getLecturerIds() {
        return lecturerIds;
    }

    public void setLecturerIds(List<Long> lecturerIds) {
        this.lecturerIds = lecturerIds;
    }
}
