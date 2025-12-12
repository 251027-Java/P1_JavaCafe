package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;

public record OrderItemsWOIDDTO(
        Integer orderId,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        String ProductName
) {}
