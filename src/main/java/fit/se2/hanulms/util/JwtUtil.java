package fit.se2.hanulms.util;

import fit.se2.hanulms.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final int jwtExpiration = 86400000; // 24 hours (though not used in createToken)

    // Method for single role (backward compatibility)
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("roles", List.of(role));
        return createToken(claims, username);
    }

    // Method for multiple roles (new functionality)
    public String generateToken(String username, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();

        // Convert enum set to string list for JSON serialization
        List<String> roleStrings = roles.stream()
                .map(Role::name)
                .collect(Collectors.toList());

        claims.put("roles", roleStrings);

        // Also store primary role for backward compatibility
        String primaryRole = roles.stream()
                .findFirst()
                .map(Role::name)
                .orElse("STUDENT");
        claims.put("role", primaryRole);

        return createToken(claims, username);
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
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

    // Overloaded method for string set
    public String generateTokenFromStringRoles(String username, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roleList = new ArrayList<>(roles);
        claims.put("roles", roleList);

        // Primary role for backward compatibility
        String primaryRole = roles.stream().findFirst().orElse("STUDENT");
        claims.put("role", primaryRole);

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256) // Use consistent algorithm
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract single role (backward compatibility)
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract all roles as string list
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List) {
                return (List<String>) rolesObj;
            }
            // Fallback: if only single role exists, create list
            String singleRole = claims.get("role", String.class);
            return singleRole != null ? List.of(singleRole) : List.of("STUDENT");
        });
    }

    // Extract all roles as Role enum set
    public Set<Role> extractRoleEnums(String token) {
        List<String> roleStrings = extractRoles(token);
        return roleStrings.stream()
                .map(roleStr -> {
                    try {
                        return Role.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        return Role.STUDENT; // Default fallback
                    }
                })
                .collect(Collectors.toSet());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Check if user has specific role
    public Boolean hasRole(String token, Role role) {
        Set<Role> userRoles = extractRoleEnums(token);
        return userRoles.contains(role);
    }

    // Check if user has specific role (string version)
    public Boolean hasRole(String token, String role) {
        List<String> userRoles = extractRoles(token);
        return userRoles.contains(role);
    }

    // Check if user has any of the specified roles
    public Boolean hasAnyRole(String token, Set<Role> requiredRoles) {
        Set<Role> userRoles = extractRoleEnums(token);
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }

    // Optional: Get the actual SecretKey if needed elsewhere
    public SecretKey getSigningKey() {
        return key;
    }
}