package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.*;
import com.project1.JavaCafe.Model.Products;
import com.project1.JavaCafe.Repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    private ProductsRepository repository;

    @InjectMocks
    private ProductsService service;

    private Products testProduct1;
    private Products testProduct2;
    private Products testProduct3;

    @BeforeEach
    void setUp() {
        // Create product entities used across tests (fields annotated for clarity)
        testProduct1 = new Products(
            "COFFEE",                
            "Espresso",              
            new BigDecimal("3.00"), 
            "Rich espresso shot",    
            "IN_STOCK"               
        );
        testProduct1.setProductId(1L);

        testProduct2 = new Products(
            "COFFEE",                
            "Latte",                 
            new BigDecimal("4.50"), 
            "Smooth latte",          
            "IN_STOCK"               
        );
        testProduct2.setProductId(2L);

        testProduct3 = new Products(
            "CUPCAKES",              
            "Vanilla Cupcake",       
            new BigDecimal("4.00"), 
            "Vanilla flavored",     
            "IN_STOCK"               
        );
        testProduct3.setProductId(3L);
    }

    @Test
    void testFindAllOrFilterByCategory_WithNullCategory() {
        // Arrange
        List<Products> allProducts = List.of(testProduct1, testProduct2, testProduct3);
        when(repository.findAll()).thenReturn(allProducts);

        // Act
        List<ProductsDTO> result = service.findAllOrFilterByCategory(null);

        // Assert: 
        // verify conversion from entity list to DTO list and ordering
        assertNotNull(result, "Resulting list should not be null");
        assertEquals(3, result.size(), "There should be three products returned");
        // Verify each DTO contains the expected product IDs
        assertEquals(1L, result.get(0).productId(), "First product ID should be 1");
        assertEquals(2L, result.get(1).productId(), "Second product ID should be 2");
        assertEquals(3L, result.get(2).productId(), "Third product ID should be 3");

        verify(repository, times(1)).findAll();
        verify(repository, never()).findByCategory(any());
    }

    @Test
    void testFindAllOrFilterByCategory_WithCategory() {
        // Arrange
        List<Products> coffeeProducts = List.of(testProduct1, testProduct2);
        when(repository.findByCategory("COFFEE")).thenReturn(coffeeProducts);

        // Act
        List<ProductsDTO> result = service.findAllOrFilterByCategory("COFFEE");

        // Assert: 
        // verify filtered list contains only coffee products and preserves names
        assertNotNull(result, "Filtered result should not be null");
        assertEquals(2, result.size(), "There should be two coffee products");
        assertEquals("COFFEE", result.get(0).category(), "Category should be COFFEE for first item");
        assertEquals("COFFEE", result.get(1).category(), "Category should be COFFEE for second item");
        assertEquals("Espresso", result.get(0).name(), "First product name should be Espresso");
        assertEquals("Latte", result.get(1).name(), "Second product name should be Latte");

        verify(repository, never()).findAll();
        verify(repository, times(1)).findByCategory("COFFEE");
    }

    @Test
    void testFindAllOrFilterByCategory_WithNullCategoryInProduct() {
        // Arrange
        Products productWithNullCategory = new Products(null, "Unnamed Product", new BigDecimal("5.00"), "Description", "IN_STOCK");
        productWithNullCategory.setProductId(4L);
        List<Products> products = List.of(productWithNullCategory);
        when(repository.findAll()).thenReturn(products);

        // Act
        List<ProductsDTO> result = service.findAllOrFilterByCategory(null);

        // Assert: 
        // when a product lacks a category it should be mapped to "Uncategorized"
        assertNotNull(result, "Result should not be null even if category is null");
        assertEquals(1, result.size(), "There should be one product returned");
        assertEquals("Uncategorized", result.get(0).category(), "Null category should be represented as 'Uncategorized'");
        assertEquals("Unnamed Product", result.get(0).name(), "Product name should match original entity");

        verify(repository, times(1)).findAll();
    }

    @Test
    void testCreate_Success() {
        // Arrange
        ProductsWOIDDTO dto = new ProductsWOIDDTO(
                "COFFEE",
                "Cappuccino",
                new BigDecimal("4.00"),
                "Frothy cappuccino",
                "IN_STOCK"
        );

        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            product.setProductId(5L);
            return product;
        });

        // Act
        ProductsDTO result = service.create(dto);

        // Assert: 
        // verify created product DTO contains the database-generated ID and same fields
        assertNotNull(result, "Created product DTO should not be null");
        assertEquals(5L, result.productId(), "Product ID should be set by repository save");
        assertEquals("COFFEE", result.category(), "Category should match DTO input");
        assertEquals("Cappuccino", result.name(), "Name should match DTO input");
        assertEquals(new BigDecimal("4.00"), result.basePrice(), "Base price should match DTO input");
        assertEquals("Frothy cappuccino", result.description(), "Description should match DTO input");
        assertEquals("IN_STOCK", result.availability(), "Availability should match DTO input");

        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        List<Products> products = List.of(testProduct1, testProduct2, testProduct3);
        when(repository.findAll()).thenReturn(products);

        // Act
        List<ProductsDTO> result = service.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).productId());
        assertEquals(2L, result.get(1).productId());
        assertEquals(3L, result.get(2).productId());

        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(repository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<ProductsDTO> result = service.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetAllMenuProducts_Success() {
        // Arrange
        List<Products> products = List.of(testProduct1, testProduct2, testProduct3);
        when(repository.findAll()).thenReturn(products);

        // Act
        List<MenuProductsDTO> result = service.getAllMenuProducts();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).productId());
        assertEquals("COFFEE", result.get(0).category());
        assertEquals("Espresso", result.get(0).name());
        assertEquals(new BigDecimal("3.00"), result.get(0).basePrice());
        assertEquals("IN_STOCK", result.get(0).availability());

        verify(repository, times(2)).findAll(); // Called twice in the method
    }

    @Test
    void testGetProductDescription_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));

        // Act
        MenuDescriptionDTO result = service.getProductDescription(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Rich espresso shot", result.description());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetProductDescription_ProductNotFound() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.getProductDescription(999L);
        });

        assertTrue(exception.getMessage().contains("Product not found with ID: 999"));
        assertEquals(404, exception.getStatusCode().value());

        verify(repository, times(1)).findById(999L);
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                1L,
                "COFFEE",
                "Updated Espresso",
                new BigDecimal("3.50"),
                "Updated description",
                "OUT_OF_STOCK"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            return product;
        });

        // Act
        ProductsDTO result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.productId());
        assertEquals("COFFEE", result.category());
        assertEquals("Updated Espresso", result.name());
        assertEquals(new BigDecimal("3.50"), result.basePrice());
        assertEquals("Updated description", result.description());
        assertEquals("OUT_OF_STOCK", result.availability());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testUpdate_ProductNotFound() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                999L,
                "COFFEE",
                "Product",
                new BigDecimal("5.00"),
                "Description",
                "IN_STOCK"
        );

        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.update(999L, dto);
        });

        assertTrue(exception.getMessage().contains("Product not found with ID: 999"));
        assertEquals(404, exception.getStatusCode().value());

        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any(Products.class));
    }

    @Test
    void testUpdate_PartialUpdate() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                1L,
                null,
                "Only Name Updated",
                null,
                null,
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            return product;
        });

        // Act
        ProductsDTO result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Only Name Updated", result.name());
        // Original values should remain
        assertEquals("COFFEE", result.category());
        assertEquals(new BigDecimal("3.00"), result.basePrice());
        assertEquals("Rich espresso shot", result.description());
        assertEquals("IN_STOCK", result.availability());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testUpdate_UpdateOnlyCategory() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                1L,
                "TEA",
                null,
                null,
                null,
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            return product;
        });

        // Act
        ProductsDTO result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("TEA", result.category());
        // Original values should remain
        assertEquals("Espresso", result.name());
        assertEquals(new BigDecimal("3.00"), result.basePrice());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testUpdate_UpdateOnlyBasePrice() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                1L,
                null,
                null,
                new BigDecimal("5.00"),
                null,
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            return product;
        });

        // Act
        ProductsDTO result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("5.00"), result.basePrice());
        // Original values should remain
        assertEquals("COFFEE", result.category());
        assertEquals("Espresso", result.name());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testUpdate_UpdateOnlyDescription() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                1L,
                null,
                null,
                null,
                "New description",
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            return product;
        });

        // Act
        ProductsDTO result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("New description", result.description());
        // Original values should remain
        assertEquals("COFFEE", result.category());
        assertEquals("Espresso", result.name());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testUpdate_UpdateOnlyAvailability() {
        // Arrange
        ProductsDTO dto = new ProductsDTO(
                1L,
                null,
                null,
                null,
                null,
                "OUT_OF_STOCK"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            return product;
        });

        // Act
        ProductsDTO result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("OUT_OF_STOCK", result.availability());
        // Original values should remain
        assertEquals("COFFEE", result.category());
        assertEquals("Espresso", result.name());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Products.class));
    }

    @Test
    void testInitializeTable_WhenTableIsEmpty() {
        // Arrange
        when(repository.count()).thenReturn(0L);
        when(repository.save(any(Products.class))).thenAnswer(invocation -> {
            Products product = invocation.getArgument(0);
            product.setProductId(1L);
            return product;
        });

        // Act
        service.initializeTable();

        // Assert
        verify(repository, times(1)).count();
        verify(repository, times(10)).save(any(Products.class)); // 10 products are created
    }

    @Test
    void testInitializeTable_WhenTableHasData() {
        // Arrange
        when(repository.count()).thenReturn(5L);

        // Act
        service.initializeTable();

        // Assert
        verify(repository, times(1)).count();
        verify(repository, never()).save(any(Products.class));
    }

    @Test
    void testFindAllOrFilterByCategory_EmptyResult() {
        // Arrange
        when(repository.findByCategory("TEA")).thenReturn(new ArrayList<>());

        // Act
        List<ProductsDTO> result = service.findAllOrFilterByCategory("TEA");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository, never()).findAll();
        verify(repository, times(1)).findByCategory("TEA");
    }
}
