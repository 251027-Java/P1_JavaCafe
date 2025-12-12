package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;

public record GOrderConfirmationDTO (
        Long orderId,
        BigDecimal finalTotal
) {}
