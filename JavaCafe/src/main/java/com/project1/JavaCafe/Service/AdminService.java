package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.AdminDashboardDTO;
import com.project1.JavaCafe.DTO.CustomerOrdersSummaryDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    // Fields
    private final ProductsService Pservice;
    private final CustomerOrdersService CService;

    // Constructor
    public AdminService(ProductsService Pservice, CustomerOrdersService CService) {
        this.Pservice = Pservice;
        this.CService = CService;
    }


    public AdminDashboardDTO getAdminDashboardData() {

        // 1. Fetch all required data using existing service methods

        List<ProductsDTO> products = Pservice.getAllProducts();
        List<CustomerOrdersSummaryDTO> orders = CService.getAllOrders();

        // 2. Combine the results into the single AdminDashboardDTO container
        return new AdminDashboardDTO(products, orders);
    }
}
