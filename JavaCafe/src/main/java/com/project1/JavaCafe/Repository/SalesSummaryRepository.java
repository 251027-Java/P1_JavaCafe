package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.SalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Needed for findByDate

public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Long> {

    /**
     * Finds a single SalesSummary record by its date.
     * Used by the scheduled job to check for existing summaries before inserting.
     * Returns an Optional because the summary for a given day might not exist yet.
     */
    Optional<SalesSummary> findByDate(LocalDate date);

    /**
     * Finds all SalesSummary records within a specified date range.
     * Used by the reporting service to aggregate data for weekly/monthly reports.
     * Note: 'Between' is inclusive (includes the start and end dates).
     */
    //List<SalesSummary> findByDateBetween(LocalDate startDate, LocalDate endDate);
}