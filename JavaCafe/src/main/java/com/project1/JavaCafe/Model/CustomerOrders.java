package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "CustomerOrders")
@Data
@NoArgsConstructor

public class CustomerOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    // Many orders can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private AppUser user;

    @Column(name = "totalCost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "orderDate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true // for OneToMany relationships with CascadeType.ALL
    )

    // Initialize the list to an empty ArrayList to prevent NullPointerException
    private List<OrderItems> orderItems = new ArrayList<>();




    public CustomerOrders(AppUser user, BigDecimal totalCost, LocalDate orderDate, String status) {
        this.user = user;
        this.totalCost = totalCost;
        this.orderDate = orderDate;
        this.status = status;
    }
}