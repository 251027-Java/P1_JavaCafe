package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.ContactSubmission;   
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {
    
}
