package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    // NEW FIELD: This establishes the one-to-many relationship in Java.
    // It tells Hibernate: "One CustomerOrder can have Many OrderItems."
    @OneToMany(
            mappedBy = "order", // This must match the field name in the OrderItem entity (e.g., private CustomerOrders order;)
            cascade = CascadeType.ALL, // If the order is deleted, delete all items associated with it.
            fetch = FetchType.LAZY     // Items are only loaded when explicitly requested (good for performance).
    )
    private List<OrderItems> orderItems;




    public CustomerOrders(AppUser user, BigDecimal totalCost, LocalDate orderDate, String status) {
        this.user = user;
        this.totalCost = totalCost;
        this.orderDate = orderDate;
        this.status = status;
    }
}