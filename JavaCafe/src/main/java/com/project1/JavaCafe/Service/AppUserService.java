package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.*;
import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Model.Products;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Repository.ProductsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {
    // Fields
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Constructor
    public AppUserService(AppUserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Methods
    public AppUserDTO create(AppUserWOIDDTO dto) {
        AppUser user = new AppUser(dto.email(), dto.password(), dto.userRole(), dto.firstName(), dto.lastName());
        return AppUserToDto(repository.save(user));
    }

    private AppUserDTO AppUserToDto(AppUser user) {
        return new AppUserDTO(
                user.getUserId(),   // 1
                user.getEmail(),    // 2
                user.getPassword(),        // 3
                user.getUserRole(),   // 4
                user.getFirstName(), // 5
                user.getLastName() // 6
        );
    }

    public AppUserDTO registerNewCustomer(RegisterCustomerDTO dto) {
        // 1. Business Logic: Check if email already exists
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // 2. Security: HASH the raw password before creating the entity
        String hashedPassword = passwordEncoder.encode(dto.password());

        // 3. Create the entity, HARDCODING the role for security
        AppUser user = new AppUser(
                dto.email(),
                hashedPassword,
                "CUSTOMER", // ROLE HARDCODED BY THE SERVER - SECURE!
                dto.firstName(),
                dto.lastName()
        );

        // 4. Save and return DTO
        return AppUserToDto(repository.save(user));
    }



    public Long getUserIdAfterLogin(String email) {

        // 1. Fetch the user object from the database using the unique email
        Optional<AppUser> userOptional = repository.findByEmail(email);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();

            // 2. Return the ID using the generated getter
            return user.getUserId(); // This gives you the ID
        } else {
            // Handle login failure or user not found scenario
            throw new RuntimeException("Authentication failed or user not found.");
        }
    }




}
