package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.ContactSubmissionDTO;
import com.project1.JavaCafe.DTO.ContactSubmissionWOIDDTO;
import com.project1.JavaCafe.Model.ContactSubmission;
import com.project1.JavaCafe.Repository.ContactSubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactSubmissionService {
    
    private final ContactSubmissionRepository repository;

    public ContactSubmissionService(ContactSubmissionRepository repository) {
        this.repository = repository;
    }

    public ContactSubmissionDTO create(ContactSubmissionWOIDDTO dto) {
        ContactSubmission submission = new ContactSubmission(
            dto.firstname(),
            dto.lastname(),
            dto.phone(),
            dto.email(),
            dto.subject(),
            dto.message()
        );
        
        ContactSubmission savedSubmission = repository.save(submission);
        return contactSubmissionToDto(savedSubmission);
    }

    private ContactSubmissionDTO contactSubmissionToDto(ContactSubmission submission) {
        return new ContactSubmissionDTO(
            submission.getSubmissionid(),
            submission.getFirstname(),
            submission.getLastname(),
            submission.getPhone(),
            submission.getEmail(),
            submission.getSubject(),
            submission.getMessage(),
            submission.getSubmittedAt()
        );
    }
}
