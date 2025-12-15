package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.AppUserDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    // --- DECLARE FIELDS ---
    private AppUser existingUser;
    private RegisterCustomerDTO validRegisterDTO;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "rawPassword123";
    private final String ENCODED_PASSWORD = "encodedHash";
    private final Long TEST_USER_ID = 1L; // Defined for the new tests


    @BeforeEach
    void setUp() {
        // Use the existing 5-argument constructor
        existingUser = new AppUser(
                TEST_EMAIL,
                ENCODED_PASSWORD,
                "CUSTOMER",
                "John",
                "Doe"
        );

        // Explicitly set the ID (Necessary for existingUser to be properly mocked)
        existingUser.setUserId(TEST_USER_ID);

        validRegisterDTO = new RegisterCustomerDTO(
                TEST_EMAIL,
                TEST_PASSWORD,
                "John",
                "Doe"
        );
    }

    // --- Test Method for Successful Registration (Your existing test) ---
    @Test
    void registerNewCustomer_success() {
        // ARRANGE (Mocks are already set up to return 'existingUser')
        when(repository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(repository.save(any(AppUser.class))).thenReturn(existingUser);

        // ACT
        AppUserDTO resultDto = appUserService.registerNewCustomer(validRegisterDTO);

        // ASSERT
        assertNotNull(resultDto);
        assertEquals(TEST_EMAIL, resultDto.email());
        verify(repository, times(1)).save(any(AppUser.class));
    }

    // --- Test Method for Email Already Exists (Your existing test) ---
    @Test
    void registerNewCustomer_emailAlreadyExists_throwsException() {
        // ARRANGE
        when(repository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));

        // ACT & ASSERT
        assertThrows(
                IllegalArgumentException.class,
                () -> appUserService.registerNewCustomer(validRegisterDTO)
        );

        // ASSERT
        verify(repository, never()).save(any(AppUser.class));
    }

    // ------------------------------------------------------------------
    // Testing getUserIdAfterLogin
    // ------------------------------------------------------------------

    @Test
    void getUserIdAfterLogin_userFound_returnsUserId() {
        // ARRANGE
        // Mock the repository to successfully find the user
        when(repository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));

        // ACT
        Long actualId = appUserService.getUserIdAfterLogin(TEST_EMAIL);

        // ASSERT
        // Verify the correct ID is returned
        assertEquals(TEST_USER_ID, actualId);

        // Verify the repository lookup happened once
        verify(repository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void getUserIdAfterLogin_userNotFound_throwsRuntimeException() {
        // ARRANGE
        // Mock the repository to return an empty Optional
        when(repository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // ACT & ASSERT
        // Verify that the expected RuntimeException is thrown
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> appUserService.getUserIdAfterLogin(TEST_EMAIL),
                "Should throw RuntimeException when user not found."
        );

        // Verify the message (optional, but good practice)
        assertTrue(thrown.getMessage().contains("Authentication failed or user not found."));
    }
}