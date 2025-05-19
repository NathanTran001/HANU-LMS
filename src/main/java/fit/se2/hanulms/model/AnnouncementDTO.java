package fit.se2.hanulms.model;

import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public class AnnouncementDTO {

    @NotEmpty(message = "Please enter the title of announcement")
    @Length(max = 20, message = "The title should be less than 20")
    private String announcementTitle;

    @NotEmpty(message = "Please enter the description of topic")
    @Length(max = 40, message = "The description should be less than 40")
    private String announcementDescription;

    private MultipartFile attachment;

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementDescription() {
        return announcementDescription;
    }

    public void setAnnouncementDescription(String announcementDescription) {
        this.announcementDescription = announcementDescription;
    }


    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }
}
