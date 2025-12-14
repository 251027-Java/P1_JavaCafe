package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.AppUserDTO;
import com.project1.JavaCafe.DTO.AppUserWOIDDTO;
import com.project1.JavaCafe.DTO.RegisterCustomerDTO;
import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService service;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        // Create a reusable AppUser entity used by tests
        testUser = new AppUser(
                "test@example.com",   
                "hashedPassword123",  
                "CUSTOMER",           
                "John",              
                "Doe"                 
        );
        testUser.setUserId(1L);
    }

    @Test
    void testCreate_Success() {
        // Arrange: 
        // create DTO representing input data for a new user
        AppUserWOIDDTO dto = new AppUserWOIDDTO(
            "newuser@example.com", 
            "password123",         
            "ADMIN",               
            "Jane",                
            "Smith"                
        );

        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setUserId(2L);
            return user;
        });

        // Act
        AppUserDTO result = service.create(dto);

        // Assert: 
        // verify the returned DTO contains the expected values from the created entity
        assertNotNull(result, "Created AppUserDTO should not be null");
        // Verify generated ID and basic identity fields
        assertEquals(2L, result.userId(), "User ID should be set to the saved entity's ID");
        assertEquals("newuser@example.com", result.email(), "Email should be mapped from DTO/entity");
        assertEquals("password123", result.password(), "Password should match saved value in this test context");
        assertEquals("ADMIN", result.userRole(), "Role should match the DTO value");
        assertEquals("Jane", result.firstName(), "First name should be Jane");
        assertEquals("Smith", result.lastName(), "Last name should be Smith");

        verify(repository, times(1)).save(any(AppUser.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testCreate_VerifyEntityCreation() {
        // Arrange: 
        // create DTO to verify entity creation internals
        AppUserWOIDDTO dto = new AppUserWOIDDTO(
            "test@example.com", 
            "rawPassword",      
            "CUSTOMER",         
            "Test",             
            "User"              
        );

        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            // Verify the entity was created with correct values
            assertEquals("test@example.com", user.getEmail());
            assertEquals("rawPassword", user.getPassword());
            assertEquals("CUSTOMER", user.getUserRole());
            assertEquals("Test", user.getFirstName());
            assertEquals("User", user.getLastName());
            
            user.setUserId(3L);
            return user;
        });

        // Act
        AppUserDTO result = service.create(dto);

        // Assert: 
        // ensure service returned a DTO and repository save was invoked
        assertNotNull(result, "Service should return an AppUserDTO after creation");
        verify(repository, times(1)).save(any(AppUser.class));
    }

    @Test
    void testRegisterNewCustomer_WithRegisterCustomerDTO_Success() {
        // Arrange: 
        // DTO representing a customer registration request
        RegisterCustomerDTO dto = new RegisterCustomerDTO(
            "customer@example.com", 
            "password123",          
            "Customer",             
            "Name"                  
        );

        when(repository.findByEmail("customer@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setUserId(4L);
            return user;
        });

        // Act
        AppUserDTO result = service.registerNewCustomer(dto);

        // Assert: 
        // check returned DTO maps expected persisted fields
        assertNotNull(result, "Register should return a populated AppUserDTO");
        assertEquals(4L, result.userId(), "Saved user should have ID 4");
        assertEquals("customer@example.com", result.email(), "Email should match registration input");
        assertEquals("hashedPassword123", result.password(), "Password should be hashed by the encoder stub");
        assertEquals("CUSTOMER", result.userRole(), "Service should normalize role to CUSTOMER");
        assertEquals("Customer", result.firstName(), "First name should match DTO");
        assertEquals("Name", result.lastName(), "Last name should match DTO");

        verify(repository, times(1)).findByEmail("customer@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(repository, times(1)).save(any(AppUser.class));
    }

    @Test
    void testRegisterNewCustomer_WithRegisterCustomerDTO_EmailAlreadyExists() {
        // Arrange
        RegisterCustomerDTO dto = new RegisterCustomerDTO(
                "existing@example.com",
                "password123",
                "Customer",
                "Name"
        );

        when(repository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.registerNewCustomer(dto);
        });

        assertEquals("Email already in use.", exception.getMessage());

        verify(repository, times(1)).findByEmail("existing@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegisterNewCustomer_WithAppUserWOIDDTO_Success() {
        // Arrange
        AppUserWOIDDTO dto = new AppUserWOIDDTO(
                "user@example.com",
                "password123",
                "ADMIN",
                "Admin",
                "User"
        );

        when(repository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword456");
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setUserId(5L);
            return user;
        });

        // Act
        AppUserDTO result = service.registerNewCustomer(dto);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.userId());
        assertEquals("user@example.com", result.email());
        assertEquals("hashedPassword456", result.password());
        assertEquals("ADMIN", result.userRole()); 
        assertEquals("Admin", result.firstName());
        assertEquals("User", result.lastName());

        verify(repository, times(1)).findByEmail("user@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(repository, times(1)).save(any(AppUser.class));
    }

    @Test
    void testRegisterNewCustomer_WithAppUserWOIDDTO_EmailAlreadyExists() {
        // Arrange
        AppUserWOIDDTO dto = new AppUserWOIDDTO(
                "existing@example.com",
                "password123",
                "ADMIN",
                "Admin",
                "User"
        );

        when(repository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.registerNewCustomer(dto);
        });

        assertEquals("Email already in use.", exception.getMessage());

        verify(repository, times(1)).findByEmail("existing@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).save(any(AppUser.class));
    }

    @Test
    void testRegisterNewCustomer_WithRegisterCustomerDTO_VerifyPasswordHashing() {
        // Arrange
        RegisterCustomerDTO dto = new RegisterCustomerDTO(
                "secure@example.com",
                "plainPassword",
                "Customer",
                "Name"
        );

        String hashedPassword = "$2a$10$hashedPasswordString";
        when(repository.findByEmail("secure@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            // Verify password was hashed
            assertEquals(hashedPassword, user.getPassword());
            user.setUserId(6L);
            return user;
        });

        // Act
        AppUserDTO result = service.registerNewCustomer(dto);

        // Assert
        assertNotNull(result);
        assertEquals(hashedPassword, result.password());
        verify(passwordEncoder, times(1)).encode("plainPassword");
    }

    @Test
    void testRegisterNewCustomer_WithRegisterCustomerDTO_VerifyRoleHardcoded() {
        // Arrange
        RegisterCustomerDTO dto = new RegisterCustomerDTO(
                "customer@example.com",
                "password",
                "Customer",
                "Name"
        );

        when(repository.findByEmail("customer@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            // Verify role is hardcoded to CUSTOMER regardless of DTO
            assertEquals("CUSTOMER", user.getUserRole());
            user.setUserId(7L);
            return user;
        });

        // Act
        AppUserDTO result = service.registerNewCustomer(dto);

        // Assert
        assertNotNull(result);
        assertEquals("CUSTOMER", result.userRole());
    }

    @Test
    void testGetUserIdAfterLogin_Success() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Long result = service.getUserIdAfterLogin("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);

        verify(repository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetUserIdAfterLogin_UserNotFound() {
        // Arrange
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.getUserIdAfterLogin("nonexistent@example.com");
        });

        assertEquals("Authentication failed or user not found.", exception.getMessage());

        verify(repository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testGetUserIdAfterLogin_DifferentUser() {
        // Arrange
        AppUser differentUser = new AppUser("different@example.com", "password", "ADMIN", "Different", "User");
        differentUser.setUserId(99L);

        when(repository.findByEmail("different@example.com")).thenReturn(Optional.of(differentUser));

        // Act
        Long result = service.getUserIdAfterLogin("different@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(99L, result);

        verify(repository, times(1)).findByEmail("different@example.com");
    }

    @Test
    void testCreate_WithNullPassword() {
        // Arrange
        AppUserWOIDDTO dto = new AppUserWOIDDTO(
                "guest@example.com",
                null,
                "GUEST",
                "Guest",
                "User"
        );

        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setUserId(8L);
            return user;
        });

        // Act
        AppUserDTO result = service.create(dto);

        // Assert
        assertNotNull(result);
        assertNull(result.password());
        assertEquals("GUEST", result.userRole());

        verify(repository, times(1)).save(any(AppUser.class));
    }

    @Test
    void testRegisterNewCustomer_WithAppUserWOIDDTO_VerifyRoleFromDTO() {
        // Arrange
        AppUserWOIDDTO dto = new AppUserWOIDDTO(
                "admin@example.com",
                "password",
                "ADMIN",
                "Admin",
                "User"
        );

        when(repository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            // Verify role comes from DTO (not hardcoded)
            assertEquals("ADMIN", user.getUserRole());
            user.setUserId(9L);
            return user;
        });

        // Act
        AppUserDTO result = service.registerNewCustomer(dto);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.userRole());
    }
}
