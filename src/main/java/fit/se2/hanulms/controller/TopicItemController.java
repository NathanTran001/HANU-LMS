package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.CourseRepository;
import fit.se2.hanulms.Repository.FileRepository;
import fit.se2.hanulms.Repository.TopicRepository;
import fit.se2.hanulms.model.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Controller
@RequestMapping(value="/myCourses/course-details")
public class TopicItemController {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TopicRepository topicRepository;

    @PostMapping (value="/{code}/{topicId}/upload-file/{type}")
    public String uploadFile (@PathVariable(value="code") String code, @PathVariable(value="type") String type, @PathVariable(value = "topicId") Long topicId, @Valid FileDTO fileDTO, BindingResult result) {
//        Course course = courseRepository.getReferenceById(code);
        Topic topic = topicRepository.getReferenceById(topicId);
        System.out.println(fileDTO.getFileTitle());
        File file = new File();
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            String errors = "Bad uploading a file";
            System.out.println("Errors :"+ errors);
            return "redirect:/myCourses/course-details/"+ code+"?error="+ errors;
        }
        else {
            // save image file
            MultipartFile attachment = fileDTO.getFileData();
            if (!attachment.isEmpty()) {
                System.out.println("Not null");
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + attachment.getOriginalFilename();

                try {
                    String uploadDir = "src/main/resources/static/images/";
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    try (InputStream inputStream = attachment.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
                file.setLink(storageFileName);
            }
            file.setType(type);
            file.setName(fileDTO.getFileTitle());
            file.setType(fileDTO.getType());
            file.setTopic(topic);
            fileRepository.save(file);
            return "redirect:/myCourses/course-details/"+ code;
        }
    }
}
