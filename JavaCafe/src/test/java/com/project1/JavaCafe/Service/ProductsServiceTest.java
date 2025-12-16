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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    // Mock the dependency (Repository)
    @Mock
    private ProductsRepository repository;

    // Inject the mock into the Service class
    @InjectMocks
    private ProductsService productsService;

    // Test Data Constants
    private final Long PRODUCT_ID = 1L;
    private final String CATEGORY_COFFEE = "COFFEE";
    private final String CATEGORY_COOKIES = "COOKIES";
    private final String NAME = "Espresso";
    private final BigDecimal PRICE = new BigDecimal("3.00");
    private final String DESCRIPTION = "A rich, bold shot.";
    private final String AVAILABILITY = "IN_STOCK";

    // Test Model Objects
    private Products espressoProduct;
    private Products cookieProduct;
    private ProductsDTO espressoDto;

    @BeforeEach
    void setUp() {
        // Setup a base Product entity
        espressoProduct = new Products(
                CATEGORY_COFFEE,
                NAME,
                PRICE,
                DESCRIPTION,
                AVAILABILITY
        );
        // Manually set the ID, as JPA does this upon saving
        espressoProduct.setProductId(PRODUCT_ID);

        // Setup another Product entity for list tests
        cookieProduct = new Products(
                CATEGORY_COOKIES,
                "Chocolate Chip",
                new BigDecimal("2.50"),
                "Gooey classic cookie.",
                AVAILABILITY
        );
        cookieProduct.setProductId(2L);

        // Setup an expected DTO for comparison
        espressoDto = new ProductsDTO(
                PRODUCT_ID,
                CATEGORY_COFFEE,
                NAME,
                PRICE,
                DESCRIPTION,
                AVAILABILITY
        );
    }

    // ------------------------------------------------------------------
    // 1. CREATE Test Methods (create)
    // ------------------------------------------------------------------

    @Test
    void create_validProduct_returnsProductsDTO() {
        // ARRANGE
        // Input DTO (without ID)
        ProductsWOIDDTO inputDto = new ProductsWOIDDTO(CATEGORY_COFFEE, NAME, PRICE, DESCRIPTION, AVAILABILITY);

        // Mock the repository save() method to return the product with the generated ID
        when(repository.save(any(Products.class))).thenReturn(espressoProduct);

        // ACT
        ProductsDTO result = productsService.create(inputDto);

        // ASSERT
        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.productId());
        assertEquals(CATEGORY_COFFEE, result.category());
        // Verify that the repository's save method was called exactly once
        verify(repository, times(1)).save(any(Products.class));
    }

    // ------------------------------------------------------------------
    // 2. READ (List/Filter) Test Methods (findAllOrFilterByCategory, getAllProducts, getAllMenuProducts)
    // ------------------------------------------------------------------

    @Test
    void findAllOrFilterByCategory_categoryIsNull_returnsAllProducts() {
        // ARRANGE
        List<Products> allProducts = Arrays.asList(espressoProduct, cookieProduct);
        when(repository.findAll()).thenReturn(allProducts);

        // ACT
        List<ProductsDTO> results = productsService.findAllOrFilterByCategory(null);

        // ASSERT
        assertFalse(results.isEmpty());
        assertEquals(2, results.size());
        assertEquals(CATEGORY_COFFEE, results.get(0).category());
        // Verify that findAll was called, and findByCategory was NOT called
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByCategory(anyString());
    }

    @Test
    void findAllOrFilterByCategory_categoryIsProvided_returnsFilteredProducts() {
        // ARRANGE
        List<Products> coffeeProducts = Collections.singletonList(espressoProduct);
        when(repository.findByCategory(CATEGORY_COFFEE)).thenReturn(coffeeProducts);

        // ACT
        List<ProductsDTO> results = productsService.findAllOrFilterByCategory(CATEGORY_COFFEE);

        // ASSERT
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(CATEGORY_COFFEE, results.get(0).category());
        // Verify that findByCategory was called with the correct argument
        verify(repository, times(1)).findByCategory(CATEGORY_COFFEE);
        verify(repository, never()).findAll();
    }

    @Test
    void getAllProducts_returnsAllProductsAsDTO() {
        // ARRANGE
        List<Products> allProducts = Arrays.asList(espressoProduct, cookieProduct);
        when(repository.findAll()).thenReturn(allProducts);

        // ACT
        List<ProductsDTO> results = productsService.getAllProducts();

        // ASSERT
        assertEquals(2, results.size());
        assertEquals(espressoProduct.getName(), results.get(0).name());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllMenuProducts_returnsAllProductsAsMenuDTO() {
        // ARRANGE
        List<Products> allProducts = Arrays.asList(espressoProduct, cookieProduct);
        when(repository.findAll()).thenReturn(allProducts);

        // ACT
        List<MenuProductsDTO> results = productsService.getAllMenuProducts();

        // ASSERT
        assertEquals(2, results.size());
        // Check a field specific to MenuProductsDTO (e.g., description should be missing/null, but we check name)
        assertEquals(espressoProduct.getName(), results.get(0).name());
        verify(repository, times(2)).findAll(); // NOTE: Your service calls findAll twice.
    }


    @Test
    void getProductDescription_productFound_returnsDescriptionDTO() {
        // ARRANGE
        when(repository.findById(PRODUCT_ID)).thenReturn(Optional.of(espressoProduct));

        // ACT
        MenuDescriptionDTO result = productsService.getProductDescription(PRODUCT_ID);

        // ASSERT
        assertNotNull(result);
        assertEquals(DESCRIPTION, result.description());
        verify(repository, times(1)).findById(PRODUCT_ID);
    }

    @Test
    void getProductDescription_productNotFound_throwsNotFoundException() {
        // ARRANGE
        when(repository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                productsService.getProductDescription(PRODUCT_ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Product not found"));
        verify(repository, times(1)).findById(PRODUCT_ID);
    }




    // ------------------------------------------------------------------
    // 4. Initialization Test Methods (initializeTable)
    // ------------------------------------------------------------------

    @Test
    void initializeTable_tableIsEmpty_initializesData() {
        // ARRANGE
        // Mock the count() method to indicate the table is empty
        when(repository.count()).thenReturn(0L);

        // ACT
        productsService.initializeTable();

        // ASSERT
        // Verify that repository.count() was checked
        verify(repository, times(1)).count();
        // Verify that save was called multiple times (for all 10 products)
        verify(repository, times(10)).save(any(Products.class));
    }

    @Test
    void initializeTable_tableIsNotEmpty_doesNothing() {
        // ARRANGE
        // Mock the count() method to indicate the table already has data
        when(repository.count()).thenReturn(5L);

        // ACT
        productsService.initializeTable();

        // ASSERT
        // Verify that repository.count() was checked
        verify(repository, times(1)).count();
        // Verify that save was NOT called
        verify(repository, never()).save(any(Products.class));
    }
}