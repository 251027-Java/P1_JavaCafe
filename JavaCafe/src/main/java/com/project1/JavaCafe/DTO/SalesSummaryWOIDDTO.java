package com.project1.JavaCafe.DTO;

import java.time.LocalDate;



public record SalesSummaryWOIDDTO(
        LocalDate date,
        int totalOrders,
        int totalItemsSold
) {}
