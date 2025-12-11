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
import java.util.ArrayList;   // Ensure this import is present
import java.util.List;        // Ensure this import is present
import java.util.Optional;
import java.util.stream.Collectors; // <-- NEW Import

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
    // The method takes the order details DTO AND the userId (a Long)
    public CustomerOrdersDTO create(CustomerOrdersWOIDDTO dto, Long userId) {

        // 1. Fetch AppUser
        AppUser user = ARepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        BigDecimal calculatedTotalCost = BigDecimal.ZERO;
        List<OrderItems> orderItemsToSave = new ArrayList<>();

        // ... (Loop remains the same: calculates cost and creates OrderItems entity) ...
        for (OrderItemsWOIDDTO itemDto : dto.items()) {
            // ... (product lookup, price calculation) ...
            Products product = PRepo.findById(itemDto.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemDto.productId()));
            // ... (exception handling) ...

            BigDecimal unitPrice = product.getBasePrice();
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

        // 5. Create the CustomerOrders entity
        String DEFAULT_STATUS = "PENDING";
        LocalDate creationDate = LocalDate.now();

        CustomerOrders order = new CustomerOrders(
                user,
                calculatedTotalCost,
                creationDate,
                DEFAULT_STATUS
        );

        // --- CRITICAL FIX START: Link child entities to the parent entity ---

        for (OrderItems item : orderItemsToSave) {
            // 1. Set the Bi-directional link from child to parent (sets the FK)
            item.setOrder(order);

            // 2. Add the child to the parent's collection (updates the parent's in-memory list)
            // Since you are using Lombok @Data, you might not have an add method, so use the setter:
            order.getOrderItems().add(item); // <-- Assumes the list is initialized (your previous fix)
        }

        // 6. Save the parent entity ONLY. The items are saved automatically via CASCADE.
        // This transaction saves the CustomerOrders and all linked OrderItems.
        CustomerOrders savedOrder = CRepo.save(order);

        // 7. Remove item saving loop (IRepo.save is no longer needed)
        // The savedOrder object now contains the full and correct orderItems list in memory.

        return orderToDetailDto(savedOrder);
    }

    private OrderItemsDTO OrderItemsToDto(OrderItems item) {
        // Requires OrderItemsDTO to be defined as a record:
        // public record OrderItemsDTO(Integer productId, String productName, Integer quantity, BigDecimal unitPrice) {}
        /*
        Integer itemId,
        Integer orderId,
        Integer productId,
        Integer Quantity,
        BigDecimal unitPrice,
        String ProductName
) {}
        * */

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
                .map(this::OrderItemsToDto) // Use the item helper method
                .collect(Collectors.toList());

        return new CustomerOrdersDTO(
                order.getOrderId(),   // 1
                order.getUser().getUserId(),    // 2
                order.getTotalCost(),        // 3
                order.getOrderDate(),   // 4
                order.getStatus(), // 5
                itemDTOs
        );
    }

    private CustomerOrdersSummaryDTO orderToSummaryDto(CustomerOrders order) {

        // No logic here to map order.getOrderItems()

        return new CustomerOrdersSummaryDTO(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getTotalCost(),
                order.getOrderDate(),
                order.getStatus()
                // No item list passed here
        );
    }

    public CustomerOrdersSummaryDTO update(Integer id, CustomerOrdersSummaryDTO dto) { // Note the DTO change

        // 1. Find the existing order entity by ID.
        // Ensure CRepo is JpaRepository<CustomerOrders, Integer>
        CustomerOrders order = CRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with ID: " + id)
                );

        // 2. Apply updates using null checks (PATCH logic)

        // Check 1: Update Status (Most common for orders)
        if (dto.status() != null) {
            order.setStatus(dto.status());
        }

        // Check 2: Update Total Cost (If total calculation needs manual override)
        if (dto.totalCost() != null) {
            order.setTotalCost(dto.totalCost());
        }

        // Check 3: Update Order Date (If required, though rare)
        if (dto.orderDate() != null) {
            order.setOrderDate(dto.orderDate());
        }

        // 3. Save the updated entity back to the database.
        CustomerOrders updatedOrder = CRepo.save(order); // Save the order entity

        // 4. Convert the saved entity back to the DTO for the response.
        // NOTE: You need to use your OrderToSummaryDto conversion method here.
        return orderToSummaryDto(updatedOrder); // Replace with your actual conversion method name
    }

    public List<CustomerOrdersSummaryDTO> getAllOrders() {

        // Define the statuses you want to include
        final List<String> targetStatuses = List.of("PENDING", "PICKUP");

        // 1. Use the new repository method to fetch only the filtered orders
        List<CustomerOrders> filteredOrders = CRepo.findByStatusIn(targetStatuses);

        // 2. Stream the filtered list and convert each entity to a DTO
        return filteredOrders.stream()
                .map(this::orderToSummaryDto)
                .toList(); // or collect(Collectors.toList());
    }

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
