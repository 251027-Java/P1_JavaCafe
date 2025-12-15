package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.*;
import com.project1.JavaCafe.Model.AppUser;
import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.OrderItems;
import com.project1.JavaCafe.Model.Products;
import com.project1.JavaCafe.Repository.AppUserRepository;
import com.project1.JavaCafe.Repository.CustomerOrdersRepository;
import com.project1.JavaCafe.Repository.OrderItemsRepository;
import com.project1.JavaCafe.Repository.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CustomerOrdersService {
    // Fields
    private final CustomerOrdersRepository CRepo;
    private final AppUserRepository ARepo;
    private final ProductsRepository PRepo;
    private final OrderItemsRepository IRepo;

    // Constructor
    public CustomerOrdersService (CustomerOrdersRepository CRepo, AppUserRepository ARepo, ProductsRepository PRepo, OrderItemsRepository IRepo){
        this.CRepo = CRepo;
        this.ARepo = ARepo;
        this.PRepo = PRepo;
        this.IRepo = IRepo;
    }

    // Methods

    public CustomerOrdersDTO createGuestOrder(GuestCheckoutDTO guestOrder) {

        // 1. FIND OR CREATE AppUser ENTRY (The FIX for returning guests)

        // Attempt to find an existing user (guest or member) with the provided email.
        AppUser user = ARepo.findByEmail(guestOrder.email())
                .orElseGet(() -> {
                    // If NO user is found (first-time guest), create a new GUEST profile.
                    AppUser newGuestUser = new AppUser(
                            guestOrder.email(),
                            null, // Password is null for guests
                            "GUEST", // Role is explicitly GUEST
                            guestOrder.firstName(),
                            guestOrder.lastName()
                    );

                    // Save and return the newly created guest user.
                    return ARepo.save(newGuestUser);
                });

        // The 'user' variable now holds the AppUser entity, whether it was found or created.

        // 2. CALCULATE AND CREATE OrderItems (This block remains UNCHANGED)

        BigDecimal calculatedTotalCost = BigDecimal.ZERO;
        List<OrderItems> orderItemsToSave = new ArrayList<>();

        // Loop for secure price calculation and OrderItems creation
        for (CartItemInputDTO itemDto : guestOrder.items()) {

            // Fetch authoritative price
            Products product = PRepo.findById(itemDto.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemDto.productId()));

            BigDecimal unitPrice = product.getBasePrice();
            BigDecimal lineItemCost = unitPrice.multiply(BigDecimal.valueOf(itemDto.quantity()));
            calculatedTotalCost = calculatedTotalCost.add(lineItemCost);

            // Create the OrderItems entity
            OrderItems item = new OrderItems(
                    null,
                    product,
                    itemDto.quantity(),
                    unitPrice
            );
            orderItemsToSave.add(item);
        }

        // 3. CREATE CustomerOrders ENTRY (Links all three)

        // Create the main Order
        CustomerOrders order = new CustomerOrders(
                user, // 🎯 Now linked to the found or newly created 'user' object
                calculatedTotalCost,
                LocalDate.now(),
                "Confirmed"
        );

        // Establish bidirectional link for cascade save
        for (OrderItems item : orderItemsToSave) {
            item.setOrder(order);
        }
        order.setOrderItems(orderItemsToSave);

        // Save the parent entity. This transaction saves CustomerOrders and all linked OrderItems.
        CustomerOrders savedOrder = CRepo.save(order);

        return orderToDetailDto(savedOrder);
    }



    // 🚀 MEMBER ORDER CREATION 🚀
    public CustomerOrdersDTO create(CustomerOrdersWOIDDTO dto, Long userId) {

        // 1. Fetch AppUser (Unchanged)
        AppUser user = ARepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // CRITICAL FIX: Ensure the items list exists before processing (Passed previous check)
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain items.");
        }

        BigDecimal calculatedTotalCost = BigDecimal.ZERO;
        List<OrderItems> orderItemsToSave = new ArrayList<>();

        // 2. Calculate and Create OrderItems
        for (OrderItemsWOIDDTO itemDto : dto.items()) {

            // 🔑 NEW FIX: Null check for quantity
            if (itemDto.quantity() == null || itemDto.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantity is missing or invalid for product ID: " + itemDto.productId());
            }

            // Fetch authoritative price
            Products product = PRepo.findById(itemDto.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found with ID: " + itemDto.productId()));

            BigDecimal unitPrice = product.getBasePrice();
            // Safe to call quantity() now that we've checked it is not null and > 0
            BigDecimal lineItemCost = unitPrice.multiply(BigDecimal.valueOf(itemDto.quantity()));
            calculatedTotalCost = calculatedTotalCost.add(lineItemCost);

            OrderItems item = new OrderItems(
                    null,
                    product,
                    itemDto.quantity(),
                    unitPrice
            );
            orderItemsToSave.add(item);
        }

        // 3. Create the CustomerOrders entity (Unchanged)
        String DEFAULT_STATUS = "PENDING";
        LocalDate creationDate = LocalDate.now();

        CustomerOrders order = new CustomerOrders(
                user,
                calculatedTotalCost,
                creationDate,
                DEFAULT_STATUS
        );

        for (OrderItems item : orderItemsToSave) {
            item.setOrder(order);
        }
        order.setOrderItems(orderItemsToSave);


        // 4. Save the parent entity ONLY. (Unchanged)
        CustomerOrders savedOrder = CRepo.save(order);


        return orderToDetailDto(savedOrder);
    }

    private OrderItemsDTO OrderItemsToDto(OrderItems item) {

        return new OrderItemsDTO(
                item.getItemId(),
                item.getOrder().getOrderId(),
                item.getProduct().getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getProduct().getName()
        );
    }

    private CustomerOrdersDTO orderToDetailDto(CustomerOrders order) {
        // 1. Map the nested OrderItems list to OrderItemsDTO list
        List<OrderItemsDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::OrderItemsToDto)
                .collect(Collectors.toList());

        return new CustomerOrdersDTO(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getTotalCost(),
                order.getOrderDate(),
                order.getStatus(),
                itemDTOs
        );
    }

    private CustomerOrdersSummaryDTO orderToSummaryDto(CustomerOrders order) {


        return new CustomerOrdersSummaryDTO(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getTotalCost(),
                order.getOrderDate(),
                order.getStatus()

        );
    }

//    public CustomerOrdersSummaryDTO update(Integer id, CustomerOrdersSummaryDTO dto) { //
//
//        // 1. Find the existing order entity by ID.
//        CustomerOrders order = CRepo.findById(id)
//                .orElseThrow(
//                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with ID: " + id)
//                );
//
//        // 2. Apply updates using null checks (PATCH logic)
//
//        if (dto.status() != null) {
//            order.setStatus(dto.status());
//        }
//
//
//        if (dto.totalCost() != null) {
//            order.setTotalCost(dto.totalCost());
//        }
//
//
//        if (dto.orderDate() != null) {
//            order.setOrderDate(dto.orderDate());
//        }
//
//
//        CustomerOrders updatedOrder = CRepo.save(order);
//
//        return orderToSummaryDto(updatedOrder); // Replace with your actual conversion method name
//    }

//    public List<CustomerOrdersSummaryDTO> getAllOrders() {
//
//        // Define the statuses you want to include
//        final List<String> targetStatuses = List.of("PENDING", "PICKUP");
//
//        // 1. Use the new repository method to fetch only the filtered orders
//        List<CustomerOrders> filteredOrders = CRepo.findByStatusIn(targetStatuses);
//
//        // 2. Stream the filtered list and convert each entity to a DTO
//        return filteredOrders.stream()
//                .map(this::orderToSummaryDto)
//                .toList(); // or collect(Collectors.toList());
//    }

    public CustomerOrdersSummaryDTO getByIdAndUserId(Integer orderId, Long userId) {

        // 1. Secure Database Lookup: Enforces Horizontal Access Control
        // CRepo.findByOrderIdAndAppUserId requires BOTH the requested Order ID
        // and the User ID extracted from the authenticated JWT token.
        Optional<CustomerOrders> order = CRepo.findByOrderIdAndUser_UserId(orderId, userId);

        if (order.isEmpty()) {
            // If the order is not found OR doesn't belong to the user, return null
            return null;
        }

        // 2. Successful Path: Convert Model to DTO and return
        // We can safely call .get() because we checked order.isEmpty() above.
        return orderToSummaryDto(order.get());
    }

    public CustomerOrdersDTO getDetailsWithItems(Integer orderId, Long userId) {

        // 1. Secure Database Lookup: Enforces Horizontal Access Control
        // CRepo.findByOrderIdAndAppUserId requires BOTH the requested Order ID
        // and the User ID extracted from the authenticated JWT token.
        Optional<CustomerOrders> order = CRepo.findByOrderIdAndUser_UserId(orderId, userId);

        if (order.isEmpty()) {
            // If the order is not found OR doesn't belong to the user, return null
            return null;
        }

        // 2. Successful Path: Convert Model to DTO and return
        // We can safely call .get() because we checked order.isEmpty() above.
        return orderToDetailDto(order.get());
    }


}
