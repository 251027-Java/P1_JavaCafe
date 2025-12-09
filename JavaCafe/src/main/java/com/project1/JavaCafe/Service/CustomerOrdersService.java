package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.CustomerOrdersDTO;
import com.project1.JavaCafe.DTO.CustomerOrdersWOIDDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.DTO.ProductsWOIDDTO;
import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.Products;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Repository.CustomerOrdersRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerOrdersService {
    // Fields
    private final CustomerOrdersRepository CRepo;
    private final AppUserRepository ARepo;

    // Constructor
    public CustomerOrdersService (CustomerOrdersRepository CRepo, AppUserRepository ARepo){
        this.CRepo = CRepo;
        this.ARepo = ARepo;
    }

    // Methods
    // The method takes the order details DTO AND the userId (a Long)
    public CustomerOrdersDTO create(CustomerOrdersWOIDDTO dto, Long userId) {

        // 1. Fetch the AppUser profile using the CORRECT method and the separate userId parameter
        AppUser user = ARepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Create the CustomerOrders entity, passing the fetched AppUser profile.
        CustomerOrders order = new CustomerOrders(
                user,
                dto.totalCost(),
                dto.orderDate(),
                dto.status()
        );

        // ... save and return ...
        CustomerOrders savedOrder = CRepo.save(order);
        return CustomerOrdersToDto(savedOrder);
    }

    private CustomerOrdersDTO CustomerOrdersToDto(CustomerOrders order) {
        return new CustomerOrdersDTO(
                order.getOrderId(),   // 1
                order.getUser().getUserId(),    // 2
                order.getTotalCost(),        // 3
                order.getOrderDate(),   // 4
                order.getStatus() // 5
        );
    }
}
