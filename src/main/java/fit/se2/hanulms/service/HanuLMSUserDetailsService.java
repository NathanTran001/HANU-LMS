package fit.se2.hanulms.service;
import fit.se2.hanulms.Repository.AdminRepository;
import fit.se2.hanulms.Repository.LecturerRepository;
import fit.se2.hanulms.Repository.StudentRepository;
import fit.se2.hanulms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HanuLMSUserDetailsService implements UserDetailsService {
    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Hard-coded admin username and password
//        if (username.equals("admin")) {
//            return User.withUsername("admin")
//                    .password("{noop}1") // Use "{noop}" to indicate plain text password
//                    .roles("ADMIN")
//                    .build();
//        } else {
            Optional<Admin> adminOptional = adminRepository.findByUsername(username);
            if (adminOptional.isPresent()) {
                return new AdminDetails(adminOptional.get());
            }

            Optional<Lecturer> lecturerOptional = lecturerRepository.findByUsername(username);
            if (lecturerOptional.isPresent()) {
                return new LecturerDetails(lecturerOptional.get());
            }

            Optional<Student> studentOptional = studentRepository.findByUsername(username);
            if (studentOptional.isPresent()) {
                return new StudentDetails(studentOptional.get());
            }

            // Handle other user types (lecturer, student) similarly
            // Here, you might retrieve user details from a database or another source
            // For simplicity, let's assume no other users exist initially
            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
    }
}
