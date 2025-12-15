package com.project1.JavaCafe;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap; // NEW: Needed to create claims map
import java.util.Map;     // NEW: Needed for Map interface

@Component
public class JwtUtil {
    // Fields
    private final SecretKey key;
    private static final long expiration = 3600; // seconds/hour

    // Constructor
    public JwtUtil( @Value("${jwt.secret}") String secret){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Methods
    /**
     * Generates a JWT token including the user's email as the subject and the role as a claim.
     * @param email The user's email address.
     * @param userRole The user's role string (e.g., "ROLE_ADMIN", "ROLE_CUSTOMER").
     * @return The signed JWT string.
     */

    public String generateToken(Long userId, String email, String userRole){ // 1. Accepts the role string

        Map<String, Object> claims = new HashMap<>();

        // 2. Add the user's role to the claims payload
        // The key "role" will be read by the security configuration.
        claims.put("role", userRole);
        claims.put("userId", userId);




        return Jwts.builder()
                .claims(claims) // 3. Include the claims map
                .subject(email)
                .issuedAt( new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiration * 1000)))
                .signWith(key)
                .compact();
    }




    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token); // throws exception if invalid
            return true;
        } catch (Exception e) {
            System.err.println("JWT Validation Failed. Type: " + e.getClass().getSimpleName() + ". Message: " + e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * Helper method to retrieve the role from the token's claims.
     */
    public String getRoleFromToken(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Long getUserIdFromToken(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }
}
