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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/cart") // domain:port/api/expenses
public class OrderController {

    private final CustomerOrdersService orderService;
    private final AppUserService appService;
    private final ProductsService productsService;

    public OrderController(CustomerOrdersService orderService, AppUserService appService, ProductsService productsService ) {
        this.orderService = orderService;
        this.appService = appService;
        this.productsService = productsService;
    }

    @GetMapping
    public ResponseEntity<List<ProductsDTO>> getProducts(
            // IMPORTANT: Change from Long categoryId to String categoryName
            @RequestParam(required = false) String categoryName
    ) {
        List<ProductsDTO> products = productsService.findAllOrFilterByCategory(categoryName);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("/guest/submit") // Use a clear endpoint path for guests
    public ResponseEntity<CustomerOrdersDTO> submitPublicOrder(
            @RequestBody GuestCheckoutDTO guestOrderDetails, // 1. Use the correct DTO
            HttpServletRequest request // Optional, but can be kept
    ) {

        // --- Authentication Logic Removed ---
        // NO token/userId check needed. The service layer handles creating the guest user.

        // Safety check: Ensure cart items and essential guest info are present
        if (guestOrderDetails.email() == null || guestOrderDetails.items() == null || guestOrderDetails.items().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // 2. Call the Order Service method, passing the complete GuestCheckoutDTO
            CustomerOrdersDTO newOrder = orderService.createGuestOrder(guestOrderDetails);

            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Handle specific exceptions from the service layer (e.g., product not found)
            // Log the error (e.getMessage())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    // Inside OrderController.java (Temporary setup)
    @PostMapping("/new") //
    public ResponseEntity<CustomerOrdersDTO> createOrder(
            @RequestBody CustomerOrdersWOIDDTO orderDetailsDTO, //Email is inside here
            HttpServletRequest request
    ) {
        // 1. Get the email from the DTO
//        String userEmail = orderDetailsDTO.email();
//
//        if (userEmail == null || userEmail.trim().isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }

        // The JwtInterceptor placed this identity here after validating the token.
        //String userEmail = (String) request.getAttribute("email");
        Long userId = (Long) request.getAttribute("userId"); // Check for casting
        if (userId == null) {
            // This case should ideally be caught by the interceptor/security filter,
            // safety check if a token was invalid or missing the claim.
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 2. Call the AppUserService to look up the ID
        //Long userId = appService.getUserIdAfterLogin(userEmail);

        // 3. Pass the userId and the rest of the DTO to the Order Service
        CustomerOrdersDTO newOrder = orderService.create(orderDetailsDTO, userId);

        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    //findOrderById
    @GetMapping("/{id}") //practice RESTful path: GET /api/orders/123
    public ResponseEntity<CustomerOrdersSummaryDTO> getById(
            @PathVariable Integer id, // Corrected type: Use Long for Order ID
            HttpServletRequest request // Required to securely get the user's identity
    ) {
        // 1. Get the authenticated user's email from the request attribute.
        // The JwtInterceptor puts this here after validating the token.
        String userEmail = (String) request.getAttribute("email");

        // Safety check: Should not happen if JwtInterceptor works correctly
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 2. Convert the secure email back to the internal user ID.
        // This call is only necessary because you stored the email in the JWT, not the ID.
        Long userId = appService.getUserIdAfterLogin(userEmail);

        // 3. Call the secure service method
        // This method ensures the order with ID 'id' belongs to 'userId'.
        CustomerOrdersSummaryDTO order = orderService.getByIdAndUserId(id, userId);

        if (order == null) {
            // Return 404 NOT FOUND if:
            // a) The order ID doesn't exist, OR
            // b) The order exists but belongs to a different user.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/{id}/items") // Maps to: GET /api/orders/123/items
    public ResponseEntity<CustomerOrdersDTO> getDetailsWithItems(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {
        // 1. Get the authenticated user's ID (same security logic as above)
        String userEmail = (String) request.getAttribute("email");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long userId = appService.getUserIdAfterLogin(userEmail);

        // 2. Call the NEW service method designed for detailed retrieval
        // Note: The service layer MUST ensure this call eagerly loads the OrderItems.
        CustomerOrdersDTO details = orderService.getDetailsWithItems(id, userId);

        if (details == null) {
            // Returns 404 if order is not found OR if user is not the owner
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 3. Return the full DTO (including the nested list of items)
        return new ResponseEntity<>(details, HttpStatus.OK);
    }


}
