package com.project1.JavaCafe.DTO;


public record RegisterCustomerDTO(
        String email,
        String password,
        String firstName,
        String lastName
) {}
