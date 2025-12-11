package com.project1.JavaCafe.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO used to transfer sales aggregation data from the backend to the frontend.
 * This DTO represents the data stored in the SalesSummary entity.
 */
public record SalesSummaryDTO(
        Long summaryId, // Include ID, useful for reporting tools or identifying the record
        LocalDateTime date,
        int totalOrders,
        int totalItemsSold
) {}