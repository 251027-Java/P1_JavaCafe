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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerOrdersServiceTest {

    @Mock
    private CustomerOrdersRepository CRepo;

    @Mock
    private AppUserRepository ARepo;

    @Mock
    private ProductsRepository PRepo;

    @Mock
    private OrderItemsRepository IRepo;

    @InjectMocks
    private CustomerOrdersService service;

    private AppUser testUser;
    private Products testProduct;
    private CustomerOrders testOrder;
    private OrderItems testOrderItem;

    @BeforeEach
    void setUp() {
        // Create a sample AppUser entity for tests
        testUser = new AppUser(
            "test@example.com", 
            "password",         
            "CUSTOMER",         
            "John",             
            "Doe"               
        );
        testUser.setUserId(1L);

        // Create a sample product used in orders
        testProduct = new Products(
            "COFFEE",                
            "Espresso",              
            new BigDecimal("3.00"), 
            "Test description",     
            "IN_STOCK"              
        );
        testProduct.setProductId(1L);

        // Create a sample order with total cost and status
        testOrder = new CustomerOrders(
            testUser,                 
            new BigDecimal("6.00"), 
            LocalDate.now(),          
            "PENDING"              
        );
        testOrder.setOrderId(1);

        // Create a sample order item linking the order and product
        testOrderItem = new OrderItems(
            testOrder,                
            testProduct,              
            2,                        
            new BigDecimal("3.00")  
        );
        testOrderItem.setItemId(1);
        testOrder.getOrderItems().add(testOrderItem);
    }

    @Test
    void testCreateGuestOrder_Success() {
        // Arrange: 
        // create cart item DTO and guest checkout DTO representing client input
        CartItemInputDTO cartItem = new CartItemInputDTO(
            1L,  
            2     
        );
        GuestCheckoutDTO guestOrder = new GuestCheckoutDTO(
            "guest@example.com",  // Guest email
            "Jane",               // First name
            "Smith",              // Last name
            List.of(cartItem)      // List of items in the cart
        );

        AppUser savedGuestUser = new AppUser("guest@example.com", null, "GUEST", "Jane", "Smith");
        savedGuestUser.setUserId(2L);

        when(ARepo.save(any(AppUser.class))).thenReturn(savedGuestUser);
        when(PRepo.findById(1L)).thenReturn(Optional.of(testProduct));
        when(CRepo.save(any(CustomerOrders.class))).thenAnswer(invocation -> {
            CustomerOrders order = invocation.getArgument(0);
            order.setOrderId(1);
            return order;
        });

        // Act
        CustomerOrdersDTO result = service.createGuestOrder(guestOrder);

        // Assert: 
        // verify returned order DTO has expected values and items
        assertNotNull(result, "Resulting CustomerOrdersDTO should not be null");
        assertEquals(1, result.orderId(), "Order ID should be set by repository save");
        assertEquals(2L, result.userId(), "User ID should be the saved guest user's ID");
        assertEquals(new BigDecimal("6.00"), result.totalCost(), "Total cost should be calculated correctly");
        assertEquals("PENDING", result.status(), "Order status should be PENDING");
        assertNotNull(result.items(), "Items list should be present in the result");
        assertEquals(1, result.items().size(), "There should be one item in the order");

        verify(ARepo, times(1)).save(any(AppUser.class));
        verify(PRepo, times(1)).findById(1L);
        verify(CRepo, times(1)).save(any(CustomerOrders.class));
    }

    @Test
    void testCreateGuestOrder_ProductNotFound() {
        // Arrange: 
        // product ID 999 does not exist, expect failure during guest order creation
        CartItemInputDTO cartItem = new CartItemInputDTO(999L, 2);
        GuestCheckoutDTO guestOrder = new GuestCheckoutDTO(
            "guest@example.com",
            "Jane",
            "Smith",
            List.of(cartItem)
        );

        AppUser savedGuestUser = new AppUser("guest@example.com", null, "GUEST", "Jane", "Smith");
        savedGuestUser.setUserId(2L);

        when(ARepo.save(any(AppUser.class))).thenReturn(savedGuestUser);
        when(PRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.createGuestOrder(guestOrder);
        });

        assertTrue(exception.getMessage().contains("Product not found with ID: 999"));
        verify(ARepo, times(1)).save(any(AppUser.class));
        verify(PRepo, times(1)).findById(999L);
        verify(CRepo, never()).save(any(CustomerOrders.class));
    }

    @Test
    void testCreate_Success() {
        // Arrange: 
        // create order item DTO and parent order DTO as would be provided by client
        OrderItemsWOIDDTO itemDto = new OrderItemsWOIDDTO(
            null,                   // Item ID (none for creation)
            1L,                     // Product ID
            2,                      // Quantity
            new BigDecimal("3.00"),// Unit price
            "Espresso"             // Product name for convenience
        );
        CustomerOrdersWOIDDTO dto = new CustomerOrdersWOIDDTO(
            "test@example.com",    // Email of ordering user
            1L,                     // User ID
            new BigDecimal("6.00"),// Total cost
            LocalDate.now(),        // Order date
            "PENDING",             // Status
            List.of(itemDto)        // Items
        );

        when(ARepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(PRepo.findById(1L)).thenReturn(Optional.of(testProduct));
        when(CRepo.save(any(CustomerOrders.class))).thenAnswer(invocation -> {
            CustomerOrders order = invocation.getArgument(0);
            order.setOrderId(1);
            return order;
        });

        // Act
        CustomerOrdersDTO result = service.create(dto, 1L);

        // Assert: 
        // verify successful creation maps entity fields into DTO correctly
        assertNotNull(result, "Created CustomerOrdersDTO should not be null");
        assertEquals(1, result.orderId(), "Order ID should be set to 1");
        assertEquals(1L, result.userId(), "User ID should match the provided user");
        assertEquals(new BigDecimal("6.00"), result.totalCost(), "Total cost should match provided value");
        assertEquals("PENDING", result.status(), "Status should be PENDING");

        verify(ARepo, times(1)).findById(1L);
        verify(PRepo, times(1)).findById(1L);
        verify(CRepo, times(1)).save(any(CustomerOrders.class));
    }

    @Test
    void testCreate_UserNotFound() {
        // Arrange
        OrderItemsWOIDDTO itemDto = new OrderItemsWOIDDTO(null, 1L, 2, new BigDecimal("3.00"), "Espresso");
        CustomerOrdersWOIDDTO dto = new CustomerOrdersWOIDDTO(
                "test@example.com",
                999L,
                new BigDecimal("6.00"),
                LocalDate.now(),
                "PENDING",
                List.of(itemDto)
        );

        when(ARepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.create(dto, 999L);
        });

        assertTrue(exception.getMessage().contains("User not found with ID: 999"));
        verify(ARepo, times(1)).findById(999L);
        verify(PRepo, never()).findById(any());
        verify(CRepo, never()).save(any(CustomerOrders.class));
    }

    @Test
    void testCreate_ProductNotFound() {
        // Arrange: 
        // create an order DTO referencing a non-existent product to test error handling
        OrderItemsWOIDDTO itemDto = new OrderItemsWOIDDTO(null, 999L, 2, new BigDecimal("3.00"), "Product");
        CustomerOrdersWOIDDTO dto = new CustomerOrdersWOIDDTO(
            "test@example.com",
            1L,
            new BigDecimal("6.00"),
            LocalDate.now(),
            "PENDING",
            List.of(itemDto)
        );

        when(ARepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(PRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.create(dto, 1L);
        });

        assertTrue(exception.getMessage().contains("Product not found with ID: 999"));
        verify(ARepo, times(1)).findById(1L);
        verify(PRepo, times(1)).findById(999L);
        verify(CRepo, never()).save(any(CustomerOrders.class));
    }

    @Test
    void testUpdate_Success() {
        // Arrange: 
        // update DTO representing summary fields to be changed
        CustomerOrdersSummaryDTO dto = new CustomerOrdersSummaryDTO(
            1,                      // Order ID to update
            1L,                     // User ID
            new BigDecimal("10.00"), // New total cost
            LocalDate.now(),        // New date (if applicable)
            "COMPLETED"            // New status
        );

        when(CRepo.findById(1)).thenReturn(Optional.of(testOrder));
        when(CRepo.save(any(CustomerOrders.class))).thenAnswer(invocation -> {
            CustomerOrders order = invocation.getArgument(0);
            return order;
        });

        // Act
        CustomerOrdersSummaryDTO result = service.update(1, dto);

        // Assert: 
        // verify update returned expected summary fields
        assertNotNull(result, "Update should return a CustomerOrdersSummaryDTO");
        assertEquals(1, result.orderId(), "Order ID should remain the same after update");
        assertEquals("COMPLETED", result.status(), "Status should be updated to COMPLETED");
        assertEquals(new BigDecimal("10.00"), result.totalCost(), "Total cost should be updated to new value");

        verify(CRepo, times(1)).findById(1);
        verify(CRepo, times(1)).save(any(CustomerOrders.class));
    }

    @Test
    void testUpdate_OrderNotFound() {
        // Arrange
        CustomerOrdersSummaryDTO dto = new CustomerOrdersSummaryDTO(
                999,
                1L,
                new BigDecimal("10.00"),
                LocalDate.now(),
                "COMPLETED"
        );

        when(CRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.update(999, dto);
        });

        assertTrue(exception.getMessage().contains("Order not found with ID: 999"));
        verify(CRepo, times(1)).findById(999);
        verify(CRepo, never()).save(any(CustomerOrders.class));
    }

    @Test
    void testUpdate_PartialUpdate() {
        // Arrange: 
        // partial update DTO (only status should change)
        CustomerOrdersSummaryDTO dto = new CustomerOrdersSummaryDTO(
            1,
            1L,
            null,   // totalCost remains unchanged
            null,   // date remains unchanged
            "PICKUP" // New status
        );

        when(CRepo.findById(1)).thenReturn(Optional.of(testOrder));
        when(CRepo.save(any(CustomerOrders.class))).thenAnswer(invocation -> {
            CustomerOrders order = invocation.getArgument(0);
            return order;
        });

        // Act
        CustomerOrdersSummaryDTO result = service.update(1, dto);

        // Assert: 
        // ensure partial update only affects provided fields (status) and leaves others intact
        assertNotNull(result, "Result should not be null after partial update");
        assertEquals("PICKUP", result.status(), "Status should be updated to PICKUP");
        // Original totalCost should remain unchanged
        assertEquals(new BigDecimal("6.00"), result.totalCost(), "Total cost should remain the original value");

        verify(CRepo, times(1)).findById(1);
        verify(CRepo, times(1)).save(any(CustomerOrders.class));
    }

    @Test
    void testGetAllOrders_Success() {
        // Arrange
        CustomerOrders order1 = new CustomerOrders(testUser, new BigDecimal("6.00"), LocalDate.now(), "PENDING");
        order1.setOrderId(1);
        CustomerOrders order2 = new CustomerOrders(testUser, new BigDecimal("10.00"), LocalDate.now(), "PICKUP");
        order2.setOrderId(2);
        List<CustomerOrders> orders = List.of(order1, order2);

        when(CRepo.findByStatusIn(List.of("PENDING", "PICKUP"))).thenReturn(orders);

        // Act
        List<CustomerOrdersSummaryDTO> result = service.getAllOrders();

        // Assert: 
        // verify returned summary list contains both orders and their IDs match
        assertNotNull(result, "Resulting orders list should not be null");
        assertEquals(2, result.size(), "There should be two orders returned");
        assertEquals(1, result.get(0).orderId(), "First returned order ID should be 1");
        assertEquals(2, result.get(1).orderId(), "Second returned order ID should be 2");

        verify(CRepo, times(1)).findByStatusIn(List.of("PENDING", "PICKUP"));
    }

    @Test
    void testGetAllOrders_EmptyList() {
        // Arrange
        when(CRepo.findByStatusIn(List.of("PENDING", "PICKUP"))).thenReturn(new ArrayList<>());

        // Act
        List<CustomerOrdersSummaryDTO> result = service.getAllOrders();

        // Assert: 
        // empty repository case should return empty list
        assertNotNull(result, "Result list should not be null even when empty");
        assertTrue(result.isEmpty(), "Result should be empty when no orders are found");

        verify(CRepo, times(1)).findByStatusIn(List.of("PENDING", "PICKUP"));
    }

    @Test
    void testGetByIdAndUserId_Success() {
        // Arrange
        when(CRepo.findByOrderIdAndUser_UserId(1, 1L)).thenReturn(Optional.of(testOrder));

        // Act
        CustomerOrdersSummaryDTO result = service.getByIdAndUserId(1, 1L);

        // Assert: 
        // verify fetched summary contains expected ID, user, cost and status
        assertNotNull(result, "Fetched summary DTO should not be null");
        assertEquals(1, result.orderId(), "Order ID should be 1");
        assertEquals(1L, result.userId(), "User ID should be 1L");
        assertEquals(new BigDecimal("6.00"), result.totalCost(), "Total cost should be 6.00");
        assertEquals("PENDING", result.status(), "Status should be PENDING");

        verify(CRepo, times(1)).findByOrderIdAndUser_UserId(1, 1L);
    }

    @Test
    void testGetByIdAndUserId_NotFound() {
        // Arrange
        when(CRepo.findByOrderIdAndUser_UserId(999, 1L)).thenReturn(Optional.empty());

        // Act
        CustomerOrdersSummaryDTO result = service.getByIdAndUserId(999, 1L);

        // Assert: 
        // when not found, service should return null
        assertNull(result, "Service should return null for a non-existent order/user pair");

        verify(CRepo, times(1)).findByOrderIdAndUser_UserId(999, 1L);
    }

    @Test
    void testGetDetailsWithItems_Success() {
        // Arrange
        when(CRepo.findByOrderIdAndUser_UserId(1, 1L)).thenReturn(Optional.of(testOrder));

        // Act
        CustomerOrdersDTO result = service.getDetailsWithItems(1, 1L);

        // Assert: 
        // detailed DTO should include order-level info and items
        assertNotNull(result, "Detailed order DTO should not be null");
        assertEquals(1, result.orderId(), "Order ID should be 1");
        assertEquals(1L, result.userId(), "User ID should be 1L");
        assertNotNull(result.items(), "Items list should be present in detailed DTO");
        assertEquals(1, result.items().size(), "There should be one item in the detailed DTO");
        assertEquals(1, result.items().get(0).itemId(), "Item ID should match the test order item");

        verify(CRepo, times(1)).findByOrderIdAndUser_UserId(1, 1L);
    }

    @Test
    void testGetDetailsWithItems_NotFound() {
        // Arrange
        when(CRepo.findByOrderIdAndUser_UserId(999, 1L)).thenReturn(Optional.empty());

        // Act
        CustomerOrdersDTO result = service.getDetailsWithItems(999, 1L);

        // Assert: 
        // when details are requested for non-existent order return null
        assertNull(result, "getDetailsWithItems should return null when order is not found");

        verify(CRepo, times(1)).findByOrderIdAndUser_UserId(999, 1L);
    }

    @Test
    void testCreateGuestOrder_MultipleItems() {
        // Arrange: 
        // second product to use in multi-item guest order
        Products product2 = new Products(
            "COFFEE",                // Category
            "Latte",                 // Name
            new BigDecimal("4.00"), // Base price
            "Test description",      // Description
            "IN_STOCK"               // Availability
        );
        product2.setProductId(2L);

        // Two cart items to simulate multiple products in a guest checkout
        CartItemInputDTO cartItem1 = new CartItemInputDTO(1L, 2);
        CartItemInputDTO cartItem2 = new CartItemInputDTO(2L, 1);
        GuestCheckoutDTO guestOrder = new GuestCheckoutDTO(
            "guest@example.com",
            "Jane",
            "Smith",
            List.of(cartItem1, cartItem2)
        );

        AppUser savedGuestUser = new AppUser("guest@example.com", null, "GUEST", "Jane", "Smith");
        savedGuestUser.setUserId(2L);

        when(ARepo.save(any(AppUser.class))).thenReturn(savedGuestUser);
        when(PRepo.findById(1L)).thenReturn(Optional.of(testProduct));
        when(PRepo.findById(2L)).thenReturn(Optional.of(product2));
        when(CRepo.save(any(CustomerOrders.class))).thenAnswer(invocation -> {
            CustomerOrders order = invocation.getArgument(0);
            order.setOrderId(1);
            return order;
        });

        // Act
        CustomerOrdersDTO result = service.createGuestOrder(guestOrder);

        // Assert: 
        // verify the total cost calculation and item count for multiple items
        assertNotNull(result, "Resulting DTO should not be null for multi-item guest order");
        assertEquals(new BigDecimal("10.00"), result.totalCost(), "Total cost should be 2*3.00 + 1*4.00 = 10.00");
        assertEquals(2, result.items().size(), "There should be two items in the order");

        verify(ARepo, times(1)).save(any(AppUser.class));
        verify(PRepo, times(1)).findById(1L);
        verify(PRepo, times(1)).findById(2L);
        verify(CRepo, times(1)).save(any(CustomerOrders.class));
    }
}
