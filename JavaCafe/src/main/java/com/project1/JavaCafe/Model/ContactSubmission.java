package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ContactSubmissions")
@Data
@NoArgsConstructor

public class ContactSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionid;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    public ContactSubmission(String firstname, String lastname, String phone, String email, String subject, String message) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.submittedAt = LocalDateTime.now();
    }
    
}
