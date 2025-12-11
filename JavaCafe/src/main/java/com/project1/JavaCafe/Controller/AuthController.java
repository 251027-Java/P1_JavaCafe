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
    public record AuthResponse(String token){}


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

        AppUser user = optionalUser.get(); // Get the AppUser object
        Long userId = user.getUserId(); // <--- Get the ID after the lookup

        // 2. Validate the password match
        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        // 3. Generate a token WITH THE USER'S ROLE
        // We now call the two-argument generateToken(email, userRole)
        String token = jwtUtil.generateToken(
                userId,
                user.getEmail(),
                user.getUserRole() // <-- CRITICAL CHANGE: Pass the role string here
        );

        // 4. Return the token
        return new AuthResponse(token);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterCustomerDTO request){
        try {
            // 1. Call the service layer to create the user, which:
            //    - Checks for existing email.
            //    - Hashes the password.
            //    - HARDCODES the role to "CUSTOMER".
            AppUserDTO user = appUserService.registerNewCustomer(request);
            //appUserService.registerNewCustomer(request);
            Long userId = user.userId();

            // 2. Automatically log the user in immediately after successful registration
            //    by generating and returning a token for the new user.
            String token = jwtUtil.generateToken(
                    userId,
                    request.email(),
                    "CUSTOMER" // We know the role is CUSTOMER, as it was hardcoded in the service
            );

            // 3. Return the token to the frontend
            return new AuthResponse(token);

        } catch (IllegalArgumentException e) {
            // Handle the specific exception thrown by AppUserService if the email exists
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Handle other unexpected errors during the process
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }
}
