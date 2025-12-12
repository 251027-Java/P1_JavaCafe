package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerOrdersSummaryDTO(
        Integer orderId,
        Long userId,
        BigDecimal totalCost,
        LocalDate orderDate,
        String status
        // NO items list here
) {}
