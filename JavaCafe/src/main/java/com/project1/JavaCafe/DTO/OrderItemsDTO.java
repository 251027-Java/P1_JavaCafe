package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    // Many orders can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private AppUser user;

    @Column(name = "totalCost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "orderDate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "status", nullable = false)
    private String status;
* */

public record OrderItemsDTO(
        Integer itemId,
        Integer orderId,
        Long productId,
        Integer Quantity,
        BigDecimal unitPrice,
        String ProductName
) {}
