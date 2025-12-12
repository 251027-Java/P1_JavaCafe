package com.project1.JavaCafe.DTO;

public record CartItemInputDTO(
        Long productId,
        Integer quantity // Note: Changed to Integer to match the DTO in the loop
) {}
