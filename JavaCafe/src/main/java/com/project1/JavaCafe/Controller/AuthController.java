package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.JwtUtil;
import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // Auth Records
    public record AuthRequest(String email, String password){}
    public record AuthResponse(String token){}


    // Fields
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructors
    public AuthController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Methods
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){
        // does the user exist?
        Optional<AppUser> user = appUserRepository.findByEmail(request.email);
        if(user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // if they do, does the password match?
        if(!passwordEncoder.matches(request.password(), user.get().getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }
        // if they do, generate a token
        String token = jwtUtil.generateToken(user.get().getEmail());
        // return the token
        return new AuthResponse(token);
    }
}
