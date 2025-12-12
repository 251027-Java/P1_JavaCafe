package com.project1.JavaCafe.DTO;

import java.time.LocalDateTime;

public record ContactSubmissionDTO(

    Long submissionId,
    String firstname,
    String lastname,
    String phone,
    String email,
    String subject,
    String message,
    LocalDateTime submittedAt
) {}
