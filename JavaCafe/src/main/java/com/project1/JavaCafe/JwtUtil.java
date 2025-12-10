package com.project1.JavaCafe;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    // Fields
//    private static final String secret = "thisIsMySuperSecretSecuritySecret";     // Moved to .env
    private final SecretKey key;
    private static final long expiration = 3600; // seconds/hour

    // Constructor
    public JwtUtil( @Value("${jwt.secret}") String secret){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    // Methods
    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt( new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiration * 1000)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token); // throws exception if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
