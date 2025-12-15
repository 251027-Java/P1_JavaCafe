package com.project1.JavaCafe;

import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Repository.CustomerOrdersRepository;
import com.project1.JavaCafe.Repository.ProductsRepository;
import com.project1.JavaCafe.Service.CustomerOrdersService;
import com.project1.JavaCafe.Service.ProductsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@SpringBootApplication
public class JavaCafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaCafeApplication.class, args);
	}

    //  NEW: Define PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


        // Bean is a single method that is run after the application is started
        @Bean
        CommandLineRunner seedData(ProductsService productsService, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
            return args -> {
                // Spring safely calls the service method here after everything is ready
                productsService.initializeTable();


                // ADD GUEST USER LOGIC
                if (appUserRepository.findByEmail("guest.user@cafe.com").isEmpty()) {

                    String adminHashedPassword = passwordEncoder.encode("guestpassword1");

                    AppUser adminUser = new AppUser(
                            "guest.user@cafe.com",
                            adminHashedPassword,
                            "GUEST", // Set role to ADMIN
                            "Cafe",
                            "Guest"
                    );
                    appUserRepository.save(adminUser);
                    System.out.println("--- Created Guest User ---");
                }


                // Check if a test user exists to prevent duplicates
                if (appUserRepository.findByEmail("test.user@cafe.com").isEmpty()) {

                    String hashedPassword = passwordEncoder.encode("password123");

                    AppUser testCustomer = new AppUser(
                            "test.user@cafe.com",
                            hashedPassword,
                            "CUSTOMER",
                            "Test",
                            "User"
                    );
                    appUserRepository.save(testCustomer);
                    System.out.println("--- Created Test Customer Profile ---");
                }

            }; 
        }
}
