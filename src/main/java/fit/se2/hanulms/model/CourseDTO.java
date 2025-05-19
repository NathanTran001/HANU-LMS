package fit.se2.hanulms.model;

import org.springframework.web.multipart.MultipartFile;

public class CourseDTO {
    private MultipartFile courseImage;

    public MultipartFile getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(MultipartFile courseImage) {
        this.courseImage = courseImage;
    }
}
