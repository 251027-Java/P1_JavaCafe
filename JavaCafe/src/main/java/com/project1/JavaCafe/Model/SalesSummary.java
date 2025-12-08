package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "SalesSummary", uniqueConstraints = {
        // If you intended to make the 'date' column unique:
        @UniqueConstraint(columnNames = {"date"})

})
@Data
@NoArgsConstructor
public class SalesSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private int totalOrders;

    @Column(nullable = false)
    private int totalItemsSold;


    // Constructor for creating sales summaries
    public SalesSummary(LocalDate date, int totalOrders, int totalItemsSold) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalItemsSold = totalItemsSold;
    }
}
