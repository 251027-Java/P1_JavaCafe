package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.AdminDashboardDTO;
import com.project1.JavaCafe.DTO.CustomerOrdersSummaryDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ProductsService Pservice;

    @Mock
    private CustomerOrdersService CService;

    @InjectMocks
    private AdminService service;

    private List<ProductsDTO> testProducts;
    private List<CustomerOrdersSummaryDTO> testOrders;

    @BeforeEach
    void setUp() {
        testProducts = new ArrayList<>();
        testProducts.add(new ProductsDTO(
            1L,                           
            "COFFEE",                    
            "Espresso",                  
            new BigDecimal("3.00"),     
            "Rich espresso shot",        
            "IN_STOCK"                   
        ));
        testProducts.add(new ProductsDTO(
            2L,                           
            "COFFEE",                    
            "Latte",                     
            new BigDecimal("4.50"),     
            "Smooth latte",              
            "IN_STOCK"                   
        ));

        testOrders = new ArrayList<>();
        testOrders.add(new CustomerOrdersSummaryDTO(
            1,                            
            1L,                           
            new BigDecimal("6.00"),     
            LocalDate.now(),              
            "PENDING"                    
        ));
        testOrders.add(new CustomerOrdersSummaryDTO(
            2,                            
            2L,                          
            new BigDecimal("10.00"),    
            LocalDate.now(),              
            "PICKUP"                     
        ));
    }

    @Test
    void testGetAdminDashboardData_Success() {
        // Arrange
        when(Pservice.getAllProducts()).thenReturn(testProducts);
        when(CService.getAllOrders()).thenReturn(testOrders);

        // Act
        AdminDashboardDTO result = service.getAdminDashboardData();

        // Assert: 
        // verify overall dashboard structure and mappings
        assertNotNull(result, "Dashboard DTO should not be null");
        assertNotNull(result.allProducts(), "Product list should be present in dashboard");
        assertNotNull(result.allOrders(), "Orders list should be present in dashboard");
        // Verify counts match the prepared test lists
        assertEquals(2, result.allProducts().size(), "There should be 2 products in the dashboard");
        assertEquals(2, result.allOrders().size(), "There should be 2 orders in the dashboard");
        // Verify specific field mappings for the first entries
        assertEquals("Espresso", result.allProducts().get(0).name(), "First product name should match the test product");
        assertEquals(1, result.allOrders().get(0).orderId(), "First order ID should match the test order");

        verify(Pservice, times(1)).getAllProducts();
        verify(CService, times(1)).getAllOrders();
    }

    @Test
    void testGetAdminDashboardData_EmptyProducts() {
        // Arrange
        when(Pservice.getAllProducts()).thenReturn(new ArrayList<>());
        when(CService.getAllOrders()).thenReturn(testOrders);

        // Act
        AdminDashboardDTO result = service.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertNotNull(result.allProducts());
        assertNotNull(result.allOrders());
        assertTrue(result.allProducts().isEmpty());
        assertEquals(2, result.allOrders().size());

        verify(Pservice, times(1)).getAllProducts();
        verify(CService, times(1)).getAllOrders();
    }

    @Test
    void testGetAdminDashboardData_EmptyOrders() {
        // Arrange
        when(Pservice.getAllProducts()).thenReturn(testProducts);
        when(CService.getAllOrders()).thenReturn(new ArrayList<>());

        // Act
        AdminDashboardDTO result = service.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertNotNull(result.allProducts());
        assertNotNull(result.allOrders());
        assertEquals(2, result.allProducts().size());
        assertTrue(result.allOrders().isEmpty());

        verify(Pservice, times(1)).getAllProducts();
        verify(CService, times(1)).getAllOrders();
    }

    @Test
    void testGetAdminDashboardData_BothEmpty() {
        // Arrange
        when(Pservice.getAllProducts()).thenReturn(new ArrayList<>());
        when(CService.getAllOrders()).thenReturn(new ArrayList<>());

        // Act
        AdminDashboardDTO result = service.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertNotNull(result.allProducts());
        assertNotNull(result.allOrders());
        assertTrue(result.allProducts().isEmpty());
        assertTrue(result.allOrders().isEmpty());

        verify(Pservice, times(1)).getAllProducts();
        verify(CService, times(1)).getAllOrders();
    }

    @Test
    void testGetAdminDashboardData_LargeDataSet() {
        // Arrange
        List<ProductsDTO> largeProductList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            largeProductList.add(new ProductsDTO(
                    (long) i,
                    "CATEGORY" + i,
                    "Product " + i,
                    new BigDecimal(i),
                    "Description " + i,
                    "IN_STOCK"
            ));
        }

        List<CustomerOrdersSummaryDTO> largeOrderList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            largeOrderList.add(new CustomerOrdersSummaryDTO(
                    i,
                    (long) i,
                    new BigDecimal(i * 10),
                    LocalDate.now(),
                    "PENDING"
            ));
        }

        when(Pservice.getAllProducts()).thenReturn(largeProductList);
        when(CService.getAllOrders()).thenReturn(largeOrderList);

        // Act
        AdminDashboardDTO result = service.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertEquals(50, result.allProducts().size());
        assertEquals(100, result.allOrders().size());
        assertEquals(1L, result.allProducts().get(0).productId());
        assertEquals(1, result.allOrders().get(0).orderId());

        verify(Pservice, times(1)).getAllProducts();
        verify(CService, times(1)).getAllOrders();
    }

    @Test
    void testGetAdminDashboardData_VerifyServiceCalls() {
        // Arrange
        when(Pservice.getAllProducts()).thenReturn(testProducts);
        when(CService.getAllOrders()).thenReturn(testOrders);

        // Act
        service.getAdminDashboardData();

        // Assert
        verify(Pservice, times(1)).getAllProducts();
        verify(CService, times(1)).getAllOrders();
        verifyNoMoreInteractions(Pservice, CService);
    }

    @Test
    void testGetAdminDashboardData_VerifyDTOStructure() {
        // Arrange
        when(Pservice.getAllProducts()).thenReturn(testProducts);
        when(CService.getAllOrders()).thenReturn(testOrders);

        // Act
        AdminDashboardDTO result = service.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertEquals(testProducts, result.allProducts());
        assertEquals(testOrders, result.allOrders());
        
        ProductsDTO firstProduct = result.allProducts().get(0);
        assertEquals(1L, firstProduct.productId());
        assertEquals("COFFEE", firstProduct.category());
        assertEquals("Espresso", firstProduct.name());
        
        CustomerOrdersSummaryDTO firstOrder = result.allOrders().get(0);
        assertEquals(1, firstOrder.orderId());
        assertEquals(1L, firstOrder.userId());
        assertEquals("PENDING", firstOrder.status());
    }
}
