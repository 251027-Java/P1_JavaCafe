package com.project1.JavaCafe.DTO;

public record CartItemInputDTO(
        Long productId,
        Integer quantity // Integer to match the DTO in the loop
) {}
