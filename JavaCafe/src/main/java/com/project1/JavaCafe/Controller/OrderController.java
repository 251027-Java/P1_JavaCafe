package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.DTO.*;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Repository.CustomerOrdersRepository;
import com.project1.JavaCafe.Service.AppUserService;
import com.project1.JavaCafe.Service.CustomerOrdersService;
import com.project1.JavaCafe.Service.ProductsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cart") // Maps to: domain:port/api/cart
public class OrderController {

    private final CustomerOrdersService orderService;
    private final AppUserService appService;
    private final ProductsService productsService;

    public OrderController(CustomerOrdersService orderService, AppUserService appService, ProductsService productsService ) {
        this.orderService = orderService;
        this.appService = appService;
        this.productsService = productsService;
    }

    // Get Products (Menu) ---
    @GetMapping
    public ResponseEntity<List<ProductsDTO>> getProducts(
            @RequestParam(required = false) String categoryName
    ) {
        List<ProductsDTO> products = productsService.findAllOrFilterByCategory(categoryName);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    // Guest Checkout ---
    @PostMapping("/guest/submit")
    public ResponseEntity<CustomerOrdersDTO> submitPublicOrder(
            @RequestBody GuestCheckoutDTO guestOrderDetails,
            HttpServletRequest request
    ) {
        if (guestOrderDetails.email() == null || guestOrderDetails.items() == null || guestOrderDetails.items().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            CustomerOrdersDTO newOrder = orderService.createGuestOrder(guestOrderDetails);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Log the error (e.getMessage())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Member Checkout
    // This is the endpoint the React frontend is looking for after successful login.
    @PostMapping("/member/submit") // Maps to: /api/cart/member/submit
    public ResponseEntity<CustomerOrdersDTO> submitMemberOrder(
            // The DTO must match the structure sent from React (items and total).
            @RequestBody CustomerOrdersWOIDDTO orderDetailsDTO,
            HttpServletRequest request
    ) {
        // 1. Get the authenticated user ID from the request attribute.
        // The JwtInterceptor places this here after validating the token.
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            // Safety check: Should not be reached if interceptor is configured correctly
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 2. Safety check: Ensure cart items are present
        if (orderDetailsDTO.items() == null || orderDetailsDTO.items().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // 3. Call Order Service method, passing the DTO and the secure userId
            // And orderService.create() is designed to handle this.
            CustomerOrdersDTO newOrder = orderService.create(orderDetailsDTO, userId);

            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Handle exceptions from the service layer (e.g., product not found, bad data)
            // Log the error (e.getMessage())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- EXISTING: Temporary createOrder is now redundant, you can remove it or keep it renamed ---
    // If '/member/submit' serves the same purpose, you should remove this method:
    /*
    @PostMapping("/new")
    public ResponseEntity<CustomerOrdersDTO> createOrder(
            @RequestBody CustomerOrdersWOIDDTO orderDetailsDTO,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CustomerOrdersDTO newOrder = orderService.create(orderDetailsDTO, userId);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }
    */


    // findOrderById ---
    @GetMapping("/{id}") //RESTful path: GET /api/orders/123
    public ResponseEntity<CustomerOrdersSummaryDTO> getById(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {

        String userEmail = (String) request.getAttribute("email");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long userId = appService.getUserIdAfterLogin(userEmail);
        CustomerOrdersSummaryDTO order = orderService.getByIdAndUserId(id, userId);

        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // getDetailsWithItems ---
    @GetMapping("/{id}/items") // Maps to: GET /api/orders/123/items
    public ResponseEntity<CustomerOrdersDTO> getDetailsWithItems(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {

        String userEmail = (String) request.getAttribute("email");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long userId = appService.getUserIdAfterLogin(userEmail);
        CustomerOrdersDTO details = orderService.getDetailsWithItems(id, userId);

        if (details == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(details, HttpStatus.OK);
    }
}