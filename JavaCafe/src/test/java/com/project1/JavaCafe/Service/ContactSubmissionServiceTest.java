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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactSubmissionServiceTest {

    // Mock the dependency (Repository)
    @Mock
    private ContactSubmissionRepository repository;

    // Inject the mock into the Service class
    @InjectMocks
    private ContactSubmissionService contactSubmissionService;

    // Test Data Constants
    private final Long SUBMISSION_ID = 5L;
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private final String PHONE = "555-1234";
    private final String EMAIL = "john.doe@example.com";
    private final String SUBJECT = "Order Inquiry";
    private final String MESSAGE = "When will my coffee arrive?";
    private final LocalDateTime SUBMITTED_AT = LocalDateTime.now();

    // Test Model/DTO Objects
    private ContactSubmissionWOIDDTO inputDto;
    private ContactSubmission savedSubmission;

    @BeforeEach
    void setUp() {
        // 1. Input DTO (WOID) used for the 'create' method call
        inputDto = new ContactSubmissionWOIDDTO(
                FIRST_NAME,
                LAST_NAME,
                PHONE,
                EMAIL,
                SUBJECT,
                MESSAGE
        );

        // 2. Model Entity representing the object *after* it's saved by the repository.
        // It must have the generated ID and the submittedAt timestamp.
        savedSubmission = new ContactSubmission(
                FIRST_NAME,
                LAST_NAME,
                PHONE,
                EMAIL,
                SUBJECT,
                MESSAGE
        );
        // Manually set the ID and timestamp as the repository would do upon saving.
        savedSubmission.setSubmissionid(SUBMISSION_ID);
        savedSubmission.setSubmittedAt(SUBMITTED_AT);
    }

    // ------------------------------------------------------------------
    // Test Method for: create(ContactSubmissionWOIDDTO dto)
    // ------------------------------------------------------------------

    @Test
    void create_validSubmission_returnsDTOWithIdAndTimestamp() {
        // ARRANGE
        // Mock the repository save() method to return the prepared 'savedSubmission'
        when(repository.save(any(ContactSubmission.class))).thenReturn(savedSubmission);

        // ACT
        ContactSubmissionDTO result = contactSubmissionService.create(inputDto);

        // ASSERT
        assertNotNull(result);

        // 1. Check DTO contents
        assertEquals(SUBMISSION_ID, result.submissionId(), "The returned DTO must contain the generated ID.");
        assertEquals(FIRST_NAME, result.firstname());
        assertEquals(EMAIL, result.email());
        assertEquals(MESSAGE, result.message());
        assertEquals(SUBMITTED_AT, result.submittedAt(), "The returned DTO must contain the submission timestamp.");

        // 2. Verify repository interaction
        // Verify that repository.save was called exactly once
        verify(repository, times(1)).save(
                // Verify that the entity passed to save() has the correct fields from the DTO
                argThat(submission -> submission.getFirstname().equals(FIRST_NAME) &&
                        submission.getEmail().equals(EMAIL) &&
                        submission.getMessage().equals(MESSAGE) &&
                        // Ensure the entity passed to save() does NOT have the ID/Timestamp yet
                        submission.getSubmissionid() == null)
        );
    }

    // Test case for handling optional/null fields (if your model allows them)
    @Test
    void create_missingPhoneAndSubject_savesCorrectly() {
        // ARRANGE
        // Create an input DTO with nulls for phone and subject
        ContactSubmissionWOIDDTO partialInputDto = new ContactSubmissionWOIDDTO(
                FIRST_NAME,
                LAST_NAME,
                null, // Phone is null
                EMAIL,
                null, // Subject is null
                MESSAGE
        );

        // Prepare the corresponding saved entity with null fields
        ContactSubmission partialSavedSubmission = new ContactSubmission(
                FIRST_NAME,
                LAST_NAME,
                null,
                EMAIL,
                null,
                MESSAGE
        );
        partialSavedSubmission.setSubmissionid(SUBMISSION_ID);
        partialSavedSubmission.setSubmittedAt(SUBMITTED_AT);

        when(repository.save(any(ContactSubmission.class))).thenReturn(partialSavedSubmission);

        // ACT
        ContactSubmissionDTO result = contactSubmissionService.create(partialInputDto);

        // ASSERT
        assertNull(result.phone());
        assertNull(result.subject());

        // Verify that the saved entity had nulls for phone/subject
        verify(repository, times(1)).save(
                argThat(submission -> submission.getPhone() == null &&
                        submission.getSubject() == null)
        );
    }
}