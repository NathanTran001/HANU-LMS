package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.UserTemplate;
import fit.se2.hanulms.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse servletResponse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("STUDENT");

            String token = jwtUtil.generateToken(loginRequest.getUsername(), role);

            // 🔐 Create secure HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // use HTTPS in production!
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Strict") // or "Lax" for cross-site needs
                    .build();

            servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // ✅ Return user info WITHOUT token in body
            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                    "username", loginRequest.getUsername(),
                    "role", role
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Bad Credentials!");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // Extract JWT from HTTP-only cookie
            String token = extractTokenFromCookie(request);
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            System.out.println("Expiration Time: " + jwtUtil.extractExpiration(token));

            if (token != null && jwtUtil.validateToken(token, username)) {
                // Get additional user details if needed
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create response object with user details including role
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("username", username);
                userResponse.put("role", role);

                // Add additional user fields if your UserDetails implementation has them
                if (userDetails instanceof UserTemplate) {
                    UserTemplate user = (UserTemplate) userDetails;
                    userResponse.put("username", user.getUsername()); // Assuming getName() method exists
                    userResponse.put("name", user.getName());
                    userResponse.put("email", user.getEmail());
                    userResponse.put("faculty", user.getFaculty());
                }

                return ResponseEntity.ok(userResponse);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching user details"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Clear the HTTP-only cookie
            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(true) // use HTTPS in production!
                    .path("/")
                    .maxAge(Duration.ZERO) // Expire immediately
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Also clear Spring Security context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error during logout"));
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}