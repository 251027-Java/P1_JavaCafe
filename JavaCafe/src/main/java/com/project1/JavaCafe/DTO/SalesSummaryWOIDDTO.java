package com.project1.JavaCafe.DTO;

import java.time.LocalDate;



public record SalesSummaryWOIDDTO(
         // Include ID, useful for reporting tools or identifying the record
        LocalDate date,
        int totalOrders,
        int totalItemsSold
) {}