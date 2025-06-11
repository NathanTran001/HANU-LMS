package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.*;
import fit.se2.hanulms.service.HanuLMSUserDetailsService;
import fit.se2.hanulms.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HanuLMSUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse servletResponse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
//            System.out.println("DEBUG - Raw authorities from authentication: " + authentication.getAuthorities());
            // Get ALL roles from authentication
            Set<String> allRoles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(auth -> auth.replace("ROLE_", "")) // Remove ROLE_ prefix if present
                    .collect(Collectors.toSet());
//            System.out.println("DEBUG - Processed roles: " + allRoles); // Add this too

            // Get the primary role for backward compatibility
            String primaryRole = allRoles.stream()
                    .findFirst()
                    .orElse("STUDENT");

            // Generate token with ALL roles, not just primary role
            String token = jwtUtil.generateTokenFromStringRoles(loginRequest.getUsername(), allRoles);

            // 🔐 Create secure HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // use HTTPS in production!
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Strict") // or "Lax" for cross-site needs
                    .build();

            servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Get all roles for response (already have this from above)
            // Set<String> allRoles = authentication.getAuthorities().stream()
            //         .map(GrantedAuthority::getAuthority)
            //         .map(auth -> auth.replace("ROLE_", ""))
            //         .collect(Collectors.toSet());

            // ✅ Return user info WITHOUT token in body
//            Map<String, Object> response = new HashMap<>();
//            response.put("user", Map.of(
//                    "username", loginRequest.getUsername(),
//                    "role", primaryRole,
//                    "roles", allRoles
//            ));

            return ResponseEntity.ok(userDetailsService.loadUserByUsername(loginRequest.getUsername()));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Bad Credentials!");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromCookie(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "No token found"));
            }

            String username = jwtUtil.extractUsername(token);

            if (jwtUtil.validateToken(token, username)) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("username", userDetails.getUsername());
                Set<String> roles = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(auth -> auth.replace("ROLE_", ""))
                        .collect(Collectors.toSet());
                userResponse.put("roles", roles);
                userResponse.put("role", roles.stream().findFirst());

                return ResponseEntity.ok(userResponse);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching user details: " + e.getMessage()));
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

    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}