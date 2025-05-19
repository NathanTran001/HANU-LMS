package fit.se2.hanulms.controller;

import fit.se2.hanulms.Repository.*;
import fit.se2.hanulms.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    PasswordEncoder p;

    @GetMapping("/listLecturer")
    public String listLecturer(Model model) {
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        model.addAttribute("allLecturers", allLecturers);
        return "/admin/lecturer-list";
    }
    @GetMapping("/listStudent")
    public String listStudent(Model model) {
        List<Student> allStudents = studentRepository.findAll();
        model.addAttribute("allStudents", allStudents);
        return "/admin/student-list";
    }
    @GetMapping("/listFaculty")
    public String listFaculty(Model model) {
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/faculty-list";
    }
    @GetMapping("/searchLecturer")
    public String searchLecturer(HttpServletRequest request, Model model) {
        String searchPhrase = request.getParameter("searchPhrase");
        List<Lecturer> allLecturers = lecturerRepository.findAll();
        List<Lecturer> lecturersToShow = new ArrayList<>();

        model.addAttribute("allLecturers", allLecturers);
        return "/admin/lecturer-list";
    }
    @GetMapping("/searchStudent")
    public String searchStudent(HttpServletRequest request, Model model) {
        model.addAttribute("lecturer", new UserTemplate());
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/create-lecturer";
    }
    @GetMapping("/searchFaculty")
    public String searchFaculty(HttpServletRequest request, Model model) {
        model.addAttribute("lecturer", new UserTemplate());
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/create-lecturer";
    }
    @GetMapping("/createLecturer")
    public String createLecturer(Model model) {
        model.addAttribute("lecturer", new UserTemplate());
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/create-lecturer";
    }
    @PostMapping("/createLecturer")
    public String createLecturer(@Valid UserTemplate userTemplate,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("lecturer", userTemplate);
            List<Faculty> allFaculties = facultyRepository.findAll();
            model.addAttribute("allFaculties", allFaculties);
            return "/admin/create-lecturer";
        } else {
            if (lecturerRepository.findByUsername(userTemplate.getUsername()).isPresent()) {
                model.addAttribute("lecturer", userTemplate);
                List<Faculty> allFaculties = facultyRepository.findAll();
                model.addAttribute("allFaculties", allFaculties);
                return "/admin/create-lecturer";
            }
            lecturerRepository.save(new Lecturer(userTemplate, p));
            return "redirect:/admin/listLecturer";
        }
    }
    @GetMapping("/editLecturer/{id}")
    public String editLecturer(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("lecturer", lecturerRepository.getReferenceById(id));
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/edit-lecturer";
    }
    @PostMapping("/editLecturer")
    public String editLecturer(@Valid UserTemplate userTemplate,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("lecturer", userTemplate);
            List<Faculty> allFaculties = facultyRepository.findAll();
            model.addAttribute("allFaculties", allFaculties);
            return "/admin/edit-lecturer";
        } else {
            Lecturer lecturerToBeEdited = lecturerRepository.findByUsername(userTemplate.getUsername()).get();
            lecturerToBeEdited.setName(userTemplate.getName());
            lecturerToBeEdited.setEmail(userTemplate.getEmail());
            lecturerToBeEdited.setPassword(p.encode(userTemplate.getPassword()));
            lecturerToBeEdited.setFaculty(userTemplate.getFaculty());
            lecturerRepository.save(lecturerToBeEdited);
            return "redirect:/admin/listLecturer";
        }
    }
    @GetMapping(value = "/deleteLecturer/{id}")
    public String deleteLecturer(@PathVariable(value = "id") Long id) {
        Lecturer thisLecturer = lecturerRepository.getReferenceById(id);
        List<Course> teachingCourses = thisLecturer.getCourses();
        for (Course c : teachingCourses) {
            c.getLecturers().remove(thisLecturer);
            courseRepository.save(c);
        }
        thisLecturer.getCourses().clear();
        lecturerRepository.save(thisLecturer);
        lecturerRepository.delete(thisLecturer);
        return "redirect:/admin/listLecturer";
    }
    @GetMapping("/createStudent")
    public String createStudent(Model model) {
        model.addAttribute("student", new UserTemplate());
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/create-student";
    }
    @PostMapping("/createStudent")
    public String createStudent(@Valid UserTemplate userTemplate,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("student", userTemplate);
            List<Faculty> allFaculties = facultyRepository.findAll();
            model.addAttribute("allFaculties", allFaculties);
            return "/admin/create-student";
        } else {
            if (studentRepository.findByUsername(userTemplate.getUsername()).isPresent()) {
                model.addAttribute("student", userTemplate);
                List<Faculty> allFaculties = facultyRepository.findAll();
                model.addAttribute("allFaculties", allFaculties);
                return "/admin/create-student";
            }
            studentRepository.save(new Student(userTemplate, p));
            return "redirect:/admin/listStudent";
        }
    }
    @GetMapping("/editStudent/{id}")
    public String editStudent(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("student", studentRepository.getReferenceById(id));
        List<Faculty> allFaculties = facultyRepository.findAll();
        model.addAttribute("allFaculties", allFaculties);
        return "/admin/edit-student";
    }
    @PostMapping("/editStudent")
    public String editStudent(@Valid UserTemplate userTemplate,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("student", userTemplate);
            List<Faculty> allFaculties = facultyRepository.findAll();
            model.addAttribute("allFaculties", allFaculties);
            return "/admin/edit-student";
        } else {
            Student studentToBeEdited = studentRepository.findByUsername(userTemplate.getUsername()).get();
            studentToBeEdited.setName(userTemplate.getName());
            studentToBeEdited.setEmail(userTemplate.getEmail());
            studentToBeEdited.setPassword(p.encode(userTemplate.getPassword()));
            studentToBeEdited.setFaculty(userTemplate.getFaculty());
            studentRepository.save(studentToBeEdited);
            return "redirect:/admin/listStudent";
        }
    }
    @GetMapping(value = "/deleteStudent/{id}")
    public String deleteStudent(@PathVariable(value = "id") Long id) {
        Student thisStudent = studentRepository.getReferenceById(id);
        List<Course> studyingCourses = thisStudent.getCourses();
        for (Course c : studyingCourses) {
            c.getStudents().remove(thisStudent);
            courseRepository.save(c);
        }
        thisStudent.getCourses().clear();
        studentRepository.save(thisStudent);
        studentRepository.delete(thisStudent);
        return "redirect:/admin/listStudent";
    }

    @GetMapping("/createFaculty")
    public String createFaculty(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "/admin/create-faculty";
    }
    @PostMapping("/createFaculty")
    public String createFaculty(@Valid Faculty faculty,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("faculty", faculty);
            return "/admin/create-faculty";
        } else {
            // Check for duplicate code before saving
            List<Faculty> allFaculties = facultyRepository.findAll();
            boolean isFacultyCodeDuplicate = false;
            for (Faculty f : allFaculties) {
                if (f.getCode().equals(faculty.getCode())) {
                    isFacultyCodeDuplicate = true;
                    break;
                }
            }
            if (isFacultyCodeDuplicate) {
                result.addError(new FieldError("faculty", "code", "Faculty code already exists"));
                model.addAttribute("faculty", faculty);
                return "/admin/create-faculty";
            } else {
                facultyRepository.save(faculty);
                return "redirect:/admin/listFaculty";
            }
        }
    }
    @GetMapping("/editFaculty/{code}")
    public String editFaculty(Model model, @PathVariable(value = "code") String code) {
        model.addAttribute("faculty", facultyRepository.getReferenceById(code));
        return "/admin/edit-faculty";
    }
    @PostMapping("/editFaculty")
    public String editFaculty(@Valid Faculty faculty,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("faculty", faculty);
            return "/admin/edit-faculty";
        } else {
            // Check for duplicate code before saving
            List<Faculty> allFaculties = facultyRepository.findAll();
            boolean isFacultyCodeDuplicate = false;
            for (Faculty f : allFaculties) {
                if (f.getCode().equals(faculty.getCode())) {
                    isFacultyCodeDuplicate = true;
                    break;
                }
            }
            if (isFacultyCodeDuplicate) {
                result.addError(new FieldError("faculty", "code", "Faculty code already exists"));
                model.addAttribute("faculty", faculty);
                return "/admin/edit-faculty";
            } else {
                facultyRepository.save(faculty);
                return "redirect:/admin/listFaculty";
            }
        }
    }
    @GetMapping(value = "/deleteFaculty/{code}")
    public String deleteFaculty(@PathVariable(value = "code") String code) {
        Faculty thisFaculty = facultyRepository.getReferenceById(code);
        List<Course> associatedCourses = thisFaculty.getCourses();
        for (Course course : associatedCourses) {
            List<Lecturer> allLecturers = lecturerRepository.findAll();
            // Clear existing associations for each lecturer
            for (Lecturer lecturer : allLecturers) {
                lecturer.getCourses().remove(course);
                lecturerRepository.save(lecturer); // Save to ensure changes are persisted
            }
            List<Topic> allTopics = topicRepository.findAll();
            for (Topic topic : allTopics) {
                List<File> allFiles = fileRepository.findAll();
                for (File f : allFiles) {
                    if (f.getTopic().equals(topic)) {
                        fileRepository.delete(f);
                    }
                }
                List<Assignment> allAssignments = assignmentRepository.findAll();
                for (Assignment a : allAssignments) {
                    if (a.getTopic().equals(topic)) {
                        assignmentRepository.delete(a);
                    }
                }
                if (topic.getCourse().equals(course)) {
                    topic.getFile().clear();
                    topic.getAssignments().clear();
                    topicRepository.save(topic);
                    topicRepository.delete(topic);
                }
            }
            List<Announcement> allAnnouncements = announcementRepository.findAll();
            for (Announcement announcement : allAnnouncements) {
                if (announcement.getCourse().equals(course)) {
                    announcementRepository.delete(announcement);
                }
            }
            // Clear existing associations for current course
            course.getLecturers().clear();
            course.getTopics().clear();
            course.getAnnouncements().clear();
            courseRepository.save(course);

            courseRepository.delete(course);
        }
        thisFaculty.getCourses().clear();
        facultyRepository.save(thisFaculty);

        List<Lecturer> associatedLecturers = thisFaculty.getLecturers();
        for (Lecturer l : associatedLecturers) {
            lecturerRepository.delete(l);
        }
        thisFaculty.getLecturers().clear();
        facultyRepository.save(thisFaculty);

        List<Student> associatedStudent = thisFaculty.getStudents();
        for (Student s : associatedStudent) {
            studentRepository.delete(s);
        }
        thisFaculty.getStudents().clear();
        facultyRepository.save(thisFaculty);

        facultyRepository.delete(thisFaculty);
        return "redirect:/admin/listFaculty";
    }

}
