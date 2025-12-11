package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "SalesSummary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date"})

})
@Data
@NoArgsConstructor
public class SalesSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    @Column(nullable = false, unique = true)
    private LocalDateTime date;

    @Column(nullable = false)
    private int totalOrders;

    @Column(nullable = false)
    private int totalItemsSold;


    // Constructor for creating sales summaries
    public SalesSummary(LocalDateTime date, int totalOrders, int totalItemsSold) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalItemsSold = totalItemsSold;
    }
}
