package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CustomerOrders")
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

    // Default constructor (required by JPA)
    public CustomerOrders() {}

    // Constructor for creating new orders (without orderId)
    public CustomerOrders(AppUser user, BigDecimal totalCost, LocalDate orderDate, String status) {
        this.user = user;
        this.totalCost = totalCost;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Full constructor with orderId (e.g., for retrieving from DB)
    public CustomerOrders(Integer orderId, AppUser user, BigDecimal totalCost, LocalDate orderDate, String status) {
        this.orderId = orderId;
        this.user = user;
        this.totalCost = totalCost;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters and setters

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
