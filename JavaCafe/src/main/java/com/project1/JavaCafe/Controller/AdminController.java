package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.DTO.AdminDashboardDTO;
import com.project1.JavaCafe.DTO.CustomerOrdersSummaryDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.DTO.SalesSummaryDTO;
import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.SalesSummary;
import com.project1.JavaCafe.Service.SalesSummaryService;
import com.project1.JavaCafe.Service.AdminService;
import com.project1.JavaCafe.Service.CustomerOrdersService;
import com.project1.JavaCafe.Service.ProductsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    //Products
    //PATCH mapping so they can modify availability

    // Fields
    private final ProductsService Pservice;
    private final CustomerOrdersService CService;
    private final AdminService adminService;
    private final SalesSummaryService SummaryService;

    // Constructor
    public AdminController(ProductsService Pservice, CustomerOrdersService CService, AdminService adminService, SalesSummaryService SummaryService) {
        this.Pservice = Pservice;
        this.CService = CService;
        this.adminService = adminService;
        this.SummaryService = SummaryService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        // This is the only method mapped to GET /api/admin, resolving the ambiguity
        AdminDashboardDTO dashboardData = adminService.getAdminDashboardData();

        return new ResponseEntity<>(dashboardData, HttpStatus.OK);
    }

    @PatchMapping("/product/{id}") //PATCH for partial updates
    // Use ResponseEntity to explicitly set the HTTP status code
    public ResponseEntity<ProductsDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductsDTO dto
    ) {
        // 1. Call the service layer method
        ProductsDTO updatedProduct = Pservice.update(id, dto);

        // 2. Return the updated DTO with a 200 OK status.
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);

        // Alternatively, if you don't need to return the body, you could use:
        // return new ResponseEntity<>(HttpStatus.NO_CONTENT); // This returns 204 No Content
    }

    @PatchMapping("/order/{id}") //PATCH for partial updates
    // Use ResponseEntity to explicitly set the HTTP status code
    public ResponseEntity<CustomerOrdersSummaryDTO> updateOrder(
            @PathVariable Integer id,
            @RequestBody CustomerOrdersSummaryDTO dto
    ) {
        // 1. Call the service layer method
        CustomerOrdersSummaryDTO updatedOrder = CService.update(id, dto);

        // 2. Return the updated DTO with a 200 OK status.
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);

        // Alternatively, if you don't need to return the body, you could use:
        // return new ResponseEntity<>(HttpStatus.NO_CONTENT); // This returns 204 No Content
    }

    @PostMapping("/sales/snapshot")
    public ResponseEntity<SalesSummaryDTO> createSalesSnapshot() {

        // 1. Call the service method to perform the calculation and database save
        SalesSummaryDTO snapshot = SummaryService.createSnapshot();

        // 2. Return the new snapshot record with a 201 Created status
        return new ResponseEntity<>(snapshot, HttpStatus.CREATED);
    }





}
