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
import org.springframework.stereotype.Service;

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

        // 1. Fetch the AppUser profile
        AppUser user = ARepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Initialize total cost calculation
        BigDecimal calculatedTotalCost = BigDecimal.ZERO;

        // ... (Steps 3 & 4: Order item loop and total calculation remain the same) ...
        List<OrderItems> orderItemsToSave = new ArrayList<>();

        for (OrderItemsWOIDDTO itemDto : dto.items()) {
            // ... (product lookup, price calculation, and adding to orderItemsToSave) ...
            Products product = PRepo.findProductIdByName(itemDto.ProductName());
            if (product == null) {
                throw new RuntimeException("Product not found: " + itemDto.ProductName());
            }

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

        // 5. Create the CustomerOrders entity, using the CALCULATED total and default status.

        // Define the default status constant
        String DEFAULT_STATUS = "PENDING";
        LocalDate creationDate = LocalDate.now();

        CustomerOrders order = new CustomerOrders(
                user,
                calculatedTotalCost, //Use the calculated total cost
                creationDate,
                DEFAULT_STATUS //Always set the status to PENDING
        );

        // 6. Save the parent CustomerOrders entity to generate the orderId
        CustomerOrders savedOrder = CRepo.save(order);

        // 7. Save all the OrderItems, linking them to the savedOrder
        for (OrderItems item : orderItemsToSave) {
            item.setOrder(savedOrder);
            IRepo.save(item);
        }

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
