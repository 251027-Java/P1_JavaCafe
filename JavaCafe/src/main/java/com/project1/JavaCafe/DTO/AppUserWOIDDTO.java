package com.project1.JavaCafe.DTO;

public record AppUserWOIDDTO (
        String email,
        String password,
        String userRole,
        String firstName,
        String lastName
) {}
