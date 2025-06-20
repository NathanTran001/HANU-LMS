package fit.se2.hanulms.service;

import fit.se2.hanulms.repository.AdminRepository;
import fit.se2.hanulms.repository.LecturerRepository;
import fit.se2.hanulms.repository.StudentRepository;
import fit.se2.hanulms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HanuLMSUserDetailsService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            return new LMSUserDetails(adminOptional.get());
        }

        Optional<Lecturer> lecturerOptional = lecturerRepository.findByUsername(username);
        if (lecturerOptional.isPresent()) {
            return new LMSUserDetails(lecturerOptional.get());
        }

        Optional<Student> studentOptional = studentRepository.findByUsername(username);
        if (studentOptional.isPresent()) {
            return new LMSUserDetails(studentOptional.get());
        }

        // Handle other user types (lecturer, student) similarly
        // Here, you might retrieve user details from a database or another source
        // For simplicity, let's assume no other users exist initially
        throw new UsernameNotFoundException("User not found with username: " + username);
//        }
    }
}
