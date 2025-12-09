package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerOrdersWOIDDTO (
        Long userId,
        BigDecimal totalCost,
        LocalDate orderDate,
        String status
) {}
