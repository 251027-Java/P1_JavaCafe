package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.DTO.CustomerOrdersDTO;
import com.project1.JavaCafe.DTO.CustomerOrdersWOIDDTO;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Repository.CustomerOrdersRepository;
import com.project1.JavaCafe.Service.AppUserService;
import com.project1.JavaCafe.Service.CustomerOrdersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/orders") // domain:port/api/expenses
public class OrderController {

    private final CustomerOrdersService orderService;
    private final AppUserService appService;

    public OrderController(CustomerOrdersService orderService, AppUserService appService ) {
        this.orderService = orderService;
        this.appService = appService;
    }



    @PostMapping("/new")
    public ResponseEntity<CustomerOrdersDTO> createOrder(
            @RequestBody CustomerOrdersWOIDDTO orderDetailsDTO,
            HttpServletRequest request // Inject HttpServletRequest to get the email
    ) {
//        // 1. Get the authenticated user's email (username) that the interceptor attached
//        String userEmail = (String) request.getAttribute("email");
//
//        if (userEmail == null) {
//            throw new SecurityException("Authentication context missing. Request rejected.");
//        }

        // 1. TEMPORARY FIX: Inject the email of your existing test user
        String userEmail = "test.user@cafe.com"; //Ensure this email exists in AppUsers table

        // 2. The security check is now implicitly bypassed.

        // 2. Call the AppUserService to convert the email (String) to the secure userId (Long)
        Long userId = appService.getUserIdAfterLogin(userEmail);

        // 3. Pass the secure userId to the Order Service
        CustomerOrdersDTO newOrder = orderService.create(orderDetailsDTO, userId);

        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

}
