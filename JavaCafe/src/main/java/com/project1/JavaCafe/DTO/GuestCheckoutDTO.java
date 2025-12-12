package com.project1.JavaCafe.DTO;

import java.util.List;

public record GuestCheckoutDTO(
        // 1. Guest Contact Information (for creating the temporary AppUser)
        String email,
        String firstName,
        String lastName,

        // 2. Cart Contents (for calculating cost and creating OrderItems)
        List<CartItemInputDTO> items
) {}
