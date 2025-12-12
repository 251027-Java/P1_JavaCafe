package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CustomerOrdersWOIDDTO (
        String email,
        Long userId,
        BigDecimal totalCost,
        LocalDate orderDate,
        String status,
        List<OrderItemsWOIDDTO> items
) {}
