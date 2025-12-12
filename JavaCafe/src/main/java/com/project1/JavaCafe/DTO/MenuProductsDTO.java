package com.project1.JavaCafe.DTO;

import java.math.BigDecimal;

public record MenuProductsDTO(
        Long productId,
        String category,
        String name,
        BigDecimal basePrice,
        String availability
) {}

    /*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String availability;
    */



