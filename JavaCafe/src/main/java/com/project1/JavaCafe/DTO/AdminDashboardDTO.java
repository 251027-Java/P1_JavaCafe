package com.project1.JavaCafe.DTO;

import java.util.List;

public record AdminDashboardDTO(
        List<ProductsDTO> allProducts,             // List of all products (the menu)
        List<CustomerOrdersSummaryDTO> allOrders   // List of orders (the summary)
) {}
