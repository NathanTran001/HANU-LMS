package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.UserTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
//    private UserRepository userRepository;

//    public AuthController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }
//    @PostMapping("/loginPerform")
//    public String loginPerform(@Valid UserTemplate ut, BindingResult result) {
//        System.out.println("What I POSTed with the login form: " + ut.toString());
//        if (result.hasErrors()) {
//            return "/auth/login";
//        }
//        return "/myCourses";
//    }
    @GetMapping(value="/logout")
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @PostMapping("/register")
    public String registerHandle(PasswordEncoder pEncoder, Model model, @Valid UserTemplate ut, BindingResult result) {
        if (!result.hasErrors()) {
//            User u = new User(ut, pEncoder);
//            userRepository.save(u);
        }
        return "register";
    }
}
