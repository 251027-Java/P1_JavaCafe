package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.DTO.AppUserDTO;
import com.project1.JavaCafe.DTO.RegisterCustomerDTO;
import com.project1.JavaCafe.JwtUtil;
import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Service.AppUserService;
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

    // RECORD: Includes all user data for the frontend
    public record AuthResponse(
            String token,
            String email,
            String firstName,
            String lastName

    ) {}


    // Fields
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AppUserService appUserService;

    // Constructors
    public AuthController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AppUserService appUserService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this. appUserService = appUserService;
    }

    // Methods
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){

        // 1. Check if the user exists
        Optional<AppUser> optionalUser = appUserRepository.findByEmail(request.email);
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        AppUser user = optionalUser.get();

        // 2. Validate the password match
        // This throws an exception if the PasswordEncoder bean is missing.
        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        // 3. Generate a token
        String token = jwtUtil.generateToken(
                user.getUserId(),
                user.getEmail(),
                user.getUserRole()
        );

        // 4. Return the expanded response to the frontend
        return new AuthResponse(
                token,
                //user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterCustomerDTO request){
        try {
            // 1. Call the service layer to create the user
            AppUserDTO user = appUserService.registerNewCustomer(request);
            Long userId = user.userId();

            // 2. Automatically log the user in immediately after successful registration
            String token = jwtUtil.generateToken(
                    userId,
                    request.email(),
                    "CUSTOMER"
            );

            // 3. Return the expanded response to the frontend
            return new AuthResponse(
                    token,
                    //userId,
                    request.email(),
                    request.firstName(),
                   request.lastName()
            );

        } catch (IllegalArgumentException e) {
            // Handle duplicate email, etc.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Handle other unexpected errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }
}