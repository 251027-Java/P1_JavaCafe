package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.ContactSubmissionDTO;
import com.project1.JavaCafe.DTO.ContactSubmissionWOIDDTO;
import com.project1.JavaCafe.Model.ContactSubmission;
import com.project1.JavaCafe.Repository.ContactSubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactSubmissionServiceTest {

    @Mock
    private ContactSubmissionRepository repository;

    @InjectMocks
    private ContactSubmissionService service;

    private ContactSubmission testSubmission;

    
    @BeforeEach
    void setUp() {
        // Create a test contact submission with sample data
        testSubmission = new ContactSubmission(
                "John",                        
                "Doe",                        
                "123-456-7890",               
                "john.doe@example.com",       
                "Test Subject",                
                "Test message content"         
        );
        testSubmission.setSubmissionid(1L);
        testSubmission.setSubmittedAt(LocalDateTime.now());
    }

    @Test
    void testCreate_Success() {
        // Arrange
        // Create a ContactSubmissionWOIDDTO (Without ID) representing the form data
        ContactSubmissionWOIDDTO dto = new ContactSubmissionWOIDDTO(
                "John",                        
                "Doe",                         
                "123-456-7890",                
                "john.doe@example.com",        
                "Test Subject",                
                "Test message content"         
        );

        // Mock the repository's save method to simulate database persistence
        when(repository.save(any(ContactSubmission.class))).thenAnswer(invocation -> {
            // Get the ContactSubmission entity that was passed to save()
            ContactSubmission submission = invocation.getArgument(0);
            // Simulate database generating an ID for the new submission
            submission.setSubmissionid(1L);
            // Simulate database setting the submission timestamp
            submission.setSubmittedAt(LocalDateTime.now());
            // Return the entity with the generated fields
            return submission;
        });

        // Act 
        // Execute the service method to create the contact submission
        ContactSubmissionDTO result = service.create(dto);

        // Assert
        // Verify that a result was returned (not null)
        assertNotNull(result, "The service should return a ContactSubmissionDTO");
        // Verify the submission ID was generated and set correctly
        assertEquals(1L, result.submissionId(), "Submission ID should be set to 1");
        // Verify all personal information fields are correctly mapped
        assertEquals("John", result.firstname(), "First name should match input");
        assertEquals("Doe", result.lastname(), "Last name should match input");
        assertEquals("123-456-7890", result.phone(), "Phone number should match input");
        assertEquals("john.doe@example.com", result.email(), "Email should match input");
        // Verify the submission content fields are correctly mapped
        assertEquals("Test Subject", result.subject(), "Subject should match input");
        assertEquals("Test message content", result.message(), "Message should match input");
        // Verify the timestamp was set (should not be null)
        assertNotNull(result.submittedAt(), "Submission timestamp should be set");
        // Verify that the repository's save method was called exactly once
        verify(repository, times(1)).save(any(ContactSubmission.class));
    }

    
    @Test
    void testCreate_WithDifferentData() {
        // Arrange 
        // Create a DTO with completely different values from the first test
        ContactSubmissionWOIDDTO dto = new ContactSubmissionWOIDDTO(
                "Jane",                        
                "Smith",                       
                "987-654-3210",                
                "jane.smith@example.com",      
                "Another Subject",             
                "Another message"              
        );

        // Mock the repository to return a submission with ID 2
        when(repository.save(any(ContactSubmission.class))).thenAnswer(invocation -> {
            ContactSubmission submission = invocation.getArgument(0);
            // Set a different ID to simulate a new database record
            submission.setSubmissionid(2L);
            // Set the current timestamp
            submission.setSubmittedAt(LocalDateTime.now());
            return submission;
        });

        // Act
        // Create the submission with the new data
        ContactSubmissionDTO result = service.create(dto);

        // Assert
        // Verify all the new data was correctly processed
        // Verify the result is not null
        assertNotNull(result, "The service should return a ContactSubmissionDTO");
        // Verify the new submission got a different ID (2 instead of 1)
        assertEquals(2L, result.submissionId(), "Submission ID should be set to 2");
        // Verify all the new values were correctly stored and returned
        assertEquals("Jane", result.firstname(), "First name should be Jane");
        assertEquals("Smith", result.lastname(), "Last name should be Smith");
        assertEquals("987-654-3210", result.phone(), "Phone should match new value");
        assertEquals("jane.smith@example.com", result.email(), "Email should match new value");
        assertEquals("Another Subject", result.subject(), "Subject should match new value");
        assertEquals("Another message", result.message(), "Message should match new value");
        // Verify the repository was called to save the submission
        verify(repository, times(1)).save(any(ContactSubmission.class));
    }

    @Test
    void testCreate_VerifyEntityCreation() {
        // Arrange 
        // Create a DTO with test values
        ContactSubmissionWOIDDTO dto = new ContactSubmissionWOIDDTO(
                "Test",                       
                "User",                       
                "555-1234",                   
                "test@example.com",           
                "Question",                 
                "I have a question"           
        );

        // Mock the repository save method and verify the entity inside the callback
        when(repository.save(any(ContactSubmission.class))).thenAnswer(invocation -> {
            // Get the ContactSubmission entity that the service created
            ContactSubmission submission = invocation.getArgument(0);
            // Verify the entity was created with correct values from the DTO
            assertEquals("Test", submission.getFirstname(), 
                    "Entity firstname should match DTO value");
            assertEquals("User", submission.getLastname(), 
                    "Entity lastname should match DTO value");
            assertEquals("555-1234", submission.getPhone(), 
                    "Entity phone should match DTO value");
            assertEquals("test@example.com", submission.getEmail(), 
                    "Entity email should match DTO value");
            assertEquals("Question", submission.getSubject(), 
                    "Entity subject should match DTO value");
            assertEquals("I have a question", submission.getMessage(), 
                    "Entity message should match DTO value");
            // Set the ID and timestamp that would be generated by the database
            submission.setSubmissionid(3L);
            submission.setSubmittedAt(LocalDateTime.now());
            return submission;
        });

        // Act
        ContactSubmissionDTO result = service.create(dto);

        // Assert 
        // Verify the result and that save was called
        assertNotNull(result, "The service should return a ContactSubmissionDTO");
        // Verify the repository save method was called
        verify(repository, times(1)).save(any(ContactSubmission.class));
    }

    @Test
    void testCreate_VerifyDTOMapping() {
        // Arrange 
        // Set up test data with a fixed timestamp for predictable testing
        ContactSubmissionWOIDDTO dto = new ContactSubmissionWOIDDTO(
                "Alice",                      
                "Brown",                      
                "111-222-3333",               
                "alice@example.com",          
                "Feedback",                   
                "Great service!"              
        );

        // Create a fixed timestamp to verify it's correctly passed through to the DTO
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        
        // Mock the repository to set a specific ID and the fixed timestamp
        when(repository.save(any(ContactSubmission.class))).thenAnswer(invocation -> {
            ContactSubmission submission = invocation.getArgument(0);
            // Set a specific submission ID
            submission.setSubmissionid(4L);
            // Set the fixed timestamp to verify it's correctly mapped to the DTO
            submission.setSubmittedAt(fixedTime);
            return submission;
        });

        // Act 
        // Create the submission
        ContactSubmissionDTO result = service.create(dto);

        // Assert 
        // Verify all fields are correctly mapped from entity to DTO
        // Verify the result is not null
        assertNotNull(result, "The service should return a ContactSubmissionDTO");
        // Verify the ID is correctly mapped from entity to DTO
        assertEquals(4L, result.submissionId(), "Submission ID should be 4");
        // Verify all personal information fields are correctly mapped
        assertEquals("Alice", result.firstname(), "First name should be mapped correctly");
        assertEquals("Brown", result.lastname(), "Last name should be mapped correctly");
        assertEquals("111-222-3333", result.phone(), "Phone should be mapped correctly");
        assertEquals("alice@example.com", result.email(), "Email should be mapped correctly");
        // Verify content fields are correctly mapped
        assertEquals("Feedback", result.subject(), "Subject should be mapped correctly");
        assertEquals("Great service!", result.message(), "Message should be mapped correctly");
        // Verify the timestamp is correctly mapped from entity to DTO
        assertEquals(fixedTime, result.submittedAt(), "Submission timestamp should be correctly mapped to DTO");
        // Verify the repository save was called
        verify(repository, times(1)).save(any(ContactSubmission.class));
    }
}
