package com.project1.JavaCafe.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Products", uniqueConstraints = {
        // This defines a constraint named 'unique_name_description' that spans two columns.
        @UniqueConstraint(columnNames = {"name", "description"})
})
@Data
@NoArgsConstructor
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String availability;

    // Constructor for creating new products
    public Products(String category, String name, BigDecimal basePrice, String description, String availability) {
        this.category = category;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.availability = availability;
    }

}
