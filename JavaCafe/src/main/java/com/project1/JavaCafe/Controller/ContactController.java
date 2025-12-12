package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.DTO.ContactSubmissionDTO;
import com.project1.JavaCafe.DTO.ContactSubmissionWOIDDTO;
import com.project1.JavaCafe.Service.ContactSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactSubmissionService contactSubmissionService;

    public ContactController(ContactSubmissionService contactSubmissionService) {
        this.contactSubmissionService = contactSubmissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ContactSubmissionDTO> submitContactForm(
            @RequestBody ContactSubmissionWOIDDTO contactSubmissionDTO
    ) {
        try {
            ContactSubmissionDTO savedSubmission = contactSubmissionService.create(contactSubmissionDTO);
            return new ResponseEntity<>(savedSubmission, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
