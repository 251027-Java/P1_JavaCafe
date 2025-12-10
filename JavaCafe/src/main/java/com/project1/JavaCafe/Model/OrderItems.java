package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;


import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "OrderItems")
@Data
@NoArgsConstructor
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    // Many items can belong to one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private CustomerOrders order;

    // Many items can belong to one product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Products product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unitPrice", nullable = false)
    private BigDecimal unitPrice;



    public OrderItems(CustomerOrders order, Products product, int quantity, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}