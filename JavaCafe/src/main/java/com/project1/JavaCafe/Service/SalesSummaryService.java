package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.SalesAggregationDTO;
import com.project1.JavaCafe.DTO.SalesSummaryDTO;
import com.project1.JavaCafe.DTO.SalesSummaryWOIDDTO;
import com.project1.JavaCafe.Model.SalesSummary;
import com.project1.JavaCafe.Repository.CustomerOrdersRepository;
import com.project1.JavaCafe.Repository.SalesSummaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class SalesSummaryService {

    private final SalesSummaryRepository salesSummaryRepository;
    private final CustomerOrdersRepository customerOrdersRepository;

    public SalesSummaryService(SalesSummaryRepository salesSummaryRepository,
                               CustomerOrdersRepository customerOrdersRepository) {
        this.salesSummaryRepository = salesSummaryRepository;
        this.customerOrdersRepository = customerOrdersRepository;
    }

//    public SalesSummaryDTO create(SalesSummaryWOIDDTO dto) {
//
//        // **(Optional, but HIGHLY Recommended: Check for existing summary)**
//        // The date field in SalesSummary is unique, so we should check before inserting.
//        if (salesSummaryRepository.findByDate(dto.date()).isPresent()) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT,
//                    "A sales summary already exists for the date: " + dto.date()
//            );
//        }
//
//        // 1. Create the SalesSummary Entity
//        // We use the SalesSummary constructor that matches the WOID DTO fields:
//        // public SalesSummary(LocalDate date, int totalOrders, int totalItemsSold)
//        SalesSummary summary = new SalesSummary(
//                dto.date(),
//                dto.totalOrders(),
//                dto.totalItemsSold()
//        );
//
//        // 2. Save the entity using the correct repository (salesSummaryRepository)
//        SalesSummary savedSummary = salesSummaryRepository.save(summary);
//
//        // 3. Convert the saved entity back to the DTO using the correct converter
//        return SalesSummaryToDto(savedSummary);
//    }

    public SalesSummaryDTO createSnapshot() {

        // 1. Get the exact time of the snapshot for the audit record
        LocalDateTime snapshotTime = LocalDateTime.now();

        // --- Removed the unique date check because you will likely take many snapshots ---
        // The previous check was for a specific date; this is now a unique point in time.

        // 2. Perform the Calculation (Needs the custom aggregation method in the Repository)
        // This calls the method that aggregates ALL historical orders.
        SalesAggregationDTO result = customerOrdersRepository.calculateAllTimeAggregates();

        // 3. Extract results, handling the case where no orders were found
        int cumulativeTotalOrders = result != null ? result.totalOrders() : 0;
        int cumulativeTotalItemsSold = result != null ? result.totalItemsSold() : 0;

        // 4. Create the SalesSummary Entity
        // Using the updated constructor: new SalesSummary(LocalDateTime snapshotTime, int totalOrders, int totalItemsSold)
        SalesSummary summary = new SalesSummary(
                snapshotTime,
                cumulativeTotalOrders,
                cumulativeTotalItemsSold
        );

        // 5. Save the entity using the correct repository
        SalesSummary savedSummary = salesSummaryRepository.save(summary);

        // 6. Convert the saved entity back to the DTO
        return SalesSummaryToDto(savedSummary);
    }



    // --- 3. DTO Conversion Helper ---
    private SalesSummaryDTO SalesSummaryToDto(SalesSummary summary) {
        return new SalesSummaryDTO(
                summary.getSummaryId(),
                summary.getDate(),
                summary.getTotalOrders(),
                summary.getTotalItemsSold()
        );
    }




}
