package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;

public record ProductsWOIDDTO (
        String category,
        String name,
        BigDecimal basePrice,
        String description,
        String availability
) {}



