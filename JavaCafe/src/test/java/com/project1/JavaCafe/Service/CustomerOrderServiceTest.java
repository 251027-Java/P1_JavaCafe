package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.*;
import com.project1.JavaCafe.Model.*;
import com.project1.JavaCafe.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerOrdersServiceTest {

    // Mock all dependencies
    @Mock private CustomerOrdersRepository CRepo;
    @Mock private AppUserRepository ARepo;
    @Mock private ProductsRepository PRepo;
    @Mock private OrderItemsRepository IRepo;

    @InjectMocks
    private CustomerOrdersService customerOrdersService;

    // Test Data Constants
    private final Long USER_ID = 100L;
    private final Integer ORDER_ID = 1;
    private final String EMAIL = "guest@example.com";
    private final String CATEGORY = "COFFEE";
    private final String DESCRIPTION = "A latte coffee";
    private final String PRODUCT_NAME = "Latte";
    private final String AVAILABILITY = "IN_STOCK";
    private final Long PRODUCT_ID = 5L;
    private final BigDecimal BASE_PRICE = new BigDecimal("4.00");
    private final int QUANTITY = 2;
    private final BigDecimal EXPECTED_TOTAL = new BigDecimal("8.00");


    // Test Model Objects
    private AppUser existingAppUser;
    private Products product;
    private CartItemInputDTO cartItemDTO;
    private CustomerOrders savedOrder;
    private OrderItems orderItem;


    @BeforeEach
    void setUp() {
        // --- CONSTRUCTORS KEPT AS REQUESTED, ADDED SETTERS FOR IDs (CRITICAL FIX) ---

        // 1. AppUser entity
        // Uses preferred constructor without ID
        existingAppUser = new AppUser(EMAIL, null, "GUEST", "Test", "Guest");
        existingAppUser.setUserId(USER_ID); // CRITICAL FIX: Set User ID

        // 2. Product entity
        // Uses preferred constructor without ID
        product = new Products(CATEGORY, PRODUCT_NAME, BASE_PRICE, DESCRIPTION, AVAILABILITY);
        product.setProductId(PRODUCT_ID); // CRITICAL FIX: Set Product ID

        // Input DTO for cart item
        cartItemDTO = new CartItemInputDTO(PRODUCT_ID, QUANTITY);

        // 3. CustomerOrders entity
        // Uses preferred constructor without ID
        savedOrder = new CustomerOrders(
                existingAppUser,
                EXPECTED_TOTAL,
                LocalDate.now(),
                "Confirmed"
        );
        savedOrder.setOrderId(ORDER_ID); // CRITICAL FIX: Set Order ID for retrieval assertions

        // 4. OrderItems entity
        orderItem = new OrderItems(savedOrder, product, QUANTITY, BASE_PRICE);
        orderItem.setItemId(ORDER_ID); // CRITICAL FIX: Set Item ID

        // Link objects for DTO conversion and entity saving logic
        savedOrder.setOrderItems(List.of(orderItem));
    }

    // ------------------------------------------------------------------
    // 1. createGuestOrder Tests
    // ------------------------------------------------------------------

    @Test
    void createGuestOrder_newGuest_createsUserAndOrder() {
        // ARRANGE
        GuestCheckoutDTO newGuestDto = new GuestCheckoutDTO(
                "new@guest.com", "New", "User", List.of(cartItemDTO)
        );

        when(ARepo.findByEmail(newGuestDto.email())).thenReturn(Optional.empty());
        when(ARepo.save(any(AppUser.class))).thenReturn(existingAppUser);
        when(PRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(CRepo.save(any(CustomerOrders.class))).thenReturn(savedOrder);

        // ACT
        CustomerOrdersDTO result = customerOrdersService.createGuestOrder(newGuestDto);

        // ASSERT
        verify(ARepo, times(1)).save(argThat(user -> user.getUserRole().equals("GUEST")));
        assertEquals(EXPECTED_TOTAL, result.totalCost());
    }

    @Test
    void createGuestOrder_existingGuest_usesExistingUserAndCreatesOrder() {
        // ARRANGE
        GuestCheckoutDTO existingGuestDto = new GuestCheckoutDTO(
                EMAIL, "Test", "Guest", List.of(cartItemDTO)
        );

        when(ARepo.findByEmail(EMAIL)).thenReturn(Optional.of(existingAppUser));
        when(PRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(CRepo.save(any(CustomerOrders.class))).thenReturn(savedOrder);

        // ACT
        customerOrdersService.createGuestOrder(existingGuestDto);

        // ASSERT
        verify(ARepo, never()).save(any(AppUser.class));
        verify(CRepo, times(1)).save(any(CustomerOrders.class));
    }

    @Test
    void createGuestOrder_productNotFound_throwsRuntimeException() {
        // ARRANGE
        GuestCheckoutDTO dto = new GuestCheckoutDTO(EMAIL, "T", "G", List.of(cartItemDTO));

        when(ARepo.findByEmail(EMAIL)).thenReturn(Optional.of(existingAppUser));
        when(PRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () ->
                customerOrdersService.createGuestOrder(dto)
        );
        verify(CRepo, never()).save(any(CustomerOrders.class));
    }

    // ------------------------------------------------------------------
    // 2. create (Member Order) Tests
    // ------------------------------------------------------------------

    @Test
    void createMemberOrder_success() {
        // ARRANGE
        OrderItemsWOIDDTO itemDto = new OrderItemsWOIDDTO(ORDER_ID, PRODUCT_ID, QUANTITY, BASE_PRICE, PRODUCT_NAME);
        List<OrderItemsWOIDDTO> itemsList = List.of(itemDto);

        // FIX: MOCK CustomerOrdersWOIDDTO to satisfy the service's dto.items() call
        CustomerOrdersWOIDDTO memberOrderDto = mock(CustomerOrdersWOIDDTO.class);
        when(memberOrderDto.items()).thenReturn(itemsList);

        when(ARepo.findById(USER_ID)).thenReturn(Optional.of(existingAppUser));
        when(PRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(CRepo.save(any(CustomerOrders.class))).thenReturn(savedOrder);

        // ACT
        CustomerOrdersDTO result = customerOrdersService.create(memberOrderDto, USER_ID);

        // ASSERT
        assertEquals(EXPECTED_TOTAL, result.totalCost());
        verify(CRepo, times(1)).save(argThat(order -> order.getStatus().equals("PENDING")));
        verify(memberOrderDto, atLeastOnce()).items();
    }


    @Test
    void createMemberOrder_invalidQuantity_throwsBadRequest() {
        // ARRANGE
        OrderItemsWOIDDTO itemDto = new OrderItemsWOIDDTO(null, PRODUCT_ID, 0, null, null);

        // FIX: MOCK CustomerOrdersWOIDDTO
        CustomerOrdersWOIDDTO memberOrderDto = mock(CustomerOrdersWOIDDTO.class);
        when(memberOrderDto.items()).thenReturn(List.of(itemDto));

        when(ARepo.findById(USER_ID)).thenReturn(Optional.of(existingAppUser));

        // ACT & ASSERT
        assertThrows(ResponseStatusException.class, () ->
                customerOrdersService.create(memberOrderDto, USER_ID)
        );
        verify(memberOrderDto, atLeastOnce()).items();

        verify(CRepo, never()).save(any());
    }

    @Test
    void createMemberOrder_userNotFound_throwsRuntimeException() {
        // ARRANGE
        CustomerOrdersWOIDDTO memberOrderDto = mock(CustomerOrdersWOIDDTO.class);
        // REMOVED: when(memberOrderDto.items()).thenReturn(Collections.emptyList());
        // The service throws an exception before calling dto.items(), so this stubbing is unnecessary.

        // Arrange the critical failing condition: User not found
        when(ARepo.findById(USER_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () ->
                customerOrdersService.create(memberOrderDto, USER_ID)
        );
        // Verify that the repository save method was never called
        verify(CRepo, never()).save(any());

        // Optional (Good Practice): Verify the items method was also never called
        verify(memberOrderDto, never()).items();
    }

    // ------------------------------------------------------------------
    // 3. Secured Retrieval Tests
    // ------------------------------------------------------------------

    @Test
    void getByIdAndUserId_orderFound_returnsSummaryDTO() {
        // ARRANGE
        when(CRepo.findByOrderIdAndUser_UserId(ORDER_ID, USER_ID)).thenReturn(Optional.of(savedOrder));

        // ACT
        CustomerOrdersSummaryDTO result = customerOrdersService.getByIdAndUserId(ORDER_ID, USER_ID);

        // ASSERT
        assertNotNull(result);
        assertEquals(ORDER_ID, result.orderId());
        assertEquals(USER_ID, result.userId());
    }

    @Test
    void getByIdAndUserId_orderNotFoundOrWrongUser_returnsNull() {
        // ARRANGE
        when(CRepo.findByOrderIdAndUser_UserId(ORDER_ID, USER_ID)).thenReturn(Optional.empty());

        // ACT
        CustomerOrdersSummaryDTO result = customerOrdersService.getByIdAndUserId(ORDER_ID, USER_ID);

        // ASSERT
        assertNull(result);
    }

    @Test
    void getDetailsWithItems_orderFound_returnsDetailDTO() {
        // ARRANGE
        when(CRepo.findByOrderIdAndUser_UserId(ORDER_ID, USER_ID)).thenReturn(Optional.of(savedOrder));

        // ACT
        CustomerOrdersDTO result = customerOrdersService.getDetailsWithItems(ORDER_ID, USER_ID);

        // ASSERT
        assertNotNull(result);
        assertEquals(ORDER_ID, result.orderId());
        assertFalse(result.items().isEmpty());
    }

    @Test
    void getDetailsWithItems_orderNotFoundOrWrongUser_returnsNull() {
        // ARRANGE
        when(CRepo.findByOrderIdAndUser_UserId(ORDER_ID, USER_ID)).thenReturn(Optional.empty());

        // ACT
        CustomerOrdersDTO result = customerOrdersService.getDetailsWithItems(ORDER_ID, USER_ID);

        // ASSERT
        assertNull(result);
    }
}