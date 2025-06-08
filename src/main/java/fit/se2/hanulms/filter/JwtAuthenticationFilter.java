package fit.se2.hanulms.filter;

import fit.se2.hanulms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get token from cookie (not Authorization header)
        String token = extractTokenFromCookie(request);

        if (token != null) {
            try {
                String username = jwtUtil.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.validateToken(token, username)) {
                        // Extract all roles from token
                        List<String> roles = jwtUtil.extractRoles(token);
//                        System.out.println("DEBUG - Raw roles from JWT: " + roles);
//                        System.out.println("DEBUG - First role: '" + roles.get(0) + "'");
                        // Convert roles to GrantedAuthority objects
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                        // Create authentication token with all authorities
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        username, null, authorities
                                );
//                        System.out.println("JWT Filter - Token: " + token);
//                        System.out.println("JWT Filter - Username: " + username);
//                        System.out.println("JWT Filter - Authorities: " + authToken.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Log the error but don't expose sensitive information
                logger.warn("JWT token validation failed for token: " +
                        (token.length() > 10 ? token.substring(0, 10) + "..." : token), e);

                // Clear any partial authentication
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from HTTP-only cookie
     */
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
}