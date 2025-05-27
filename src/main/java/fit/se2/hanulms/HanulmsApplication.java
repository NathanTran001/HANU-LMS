package fit.se2.hanulms;

import fit.se2.hanulms.Repository.AdminRepository;
import fit.se2.hanulms.model.Admin;
import fit.se2.hanulms.model.UserTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HanulmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HanulmsApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			UserTemplate ut = new UserTemplate();
			ut.setUsername("Nghia");
			ut.setPassword("123");
			if (adminRepository.findByUsername("Nghia").isEmpty()) {
				Admin admin = new Admin(ut, passwordEncoder);
				adminRepository.save(admin);
			}
		};
	}


}
