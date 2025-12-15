package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.*;
import com.project1.JavaCafe.Model.Products;
//import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.Repository.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductsService {
    // Fields
    private final ProductsRepository repository;

    // Constructor
    public ProductsService (ProductsRepository repository){
        this.repository = repository;
    }



    // Methods
    private MenuProductsDTO productToMenuDto(Products product) {

        return new MenuProductsDTO(
                product.getProductId(),
                product.getCategory(),
                product.getName(),
                product.getBasePrice(),
                product.getAvailability()
                // No item list passed here
        );
    }

    public List<ProductsDTO> findAllOrFilterByCategory(String categoryName) {
        List<Products> products;

        if (categoryName == null) {
            // Case 1: "All" products (No filter applied)
            products = repository.findAll();
        } else {
            // Case 2: Filtered by category name
            products = repository.findByCategory(categoryName);
        }

        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductsDTO convertToDto(Products product) {
        String catName = product.getCategory() != null ? product.getCategory() : "Uncategorized";

        return new ProductsDTO(
                product.getProductId(),
                catName, // Uses the category name
                product.getName(),
                product.getBasePrice(),
                product.getDescription(),
                product.getAvailability()
        );
    }


    public ProductsDTO create(ProductsWOIDDTO dto) {
        Products product = new Products(dto.category(), dto.name(), dto.basePrice(), dto.description(), dto.availability());
        return ProductsToDto(repository.save(product));
    }

    private ProductsDTO ProductsToDto(Products product) {
        return new ProductsDTO(
                product.getProductId(),  
                product.getCategory(),   
                product.getName(),       
                product.getBasePrice(),  
                product.getDescription(),
                product.getAvailability() 
        );
    }

    public List<ProductsDTO> getAllProducts() {
        return repository.findAll().stream().map(this::ProductsToDto).toList();
    }

    private MenuProductsDTO ProductToMenuDto(Products product) {
        return new MenuProductsDTO(

                // 1. Category
                product.getProductId(),
                product.getCategory(),

                // 2. Name
                product.getName(),

                // 3. Price
                product.getBasePrice(),

                // 4. Availability
                product.getAvailability()

        );
    }

    public List<MenuProductsDTO> getAllMenuProducts() {
        return repository.findAll().stream().map(this::ProductToMenuDto).toList();
    }

    public MenuDescriptionDTO getProductDescription(Long productId) {

        Products product = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + productId)
                );

        return new MenuDescriptionDTO(product.getDescription());
    }

    public ProductsDTO update(Long id, ProductsDTO dto) {

        Products product = repository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id)
                );

        if (dto.category() != null) {
            product.setCategory(dto.category());
        }

        if (dto.name() != null) {
            product.setName(dto.name());
        }

        if (dto.basePrice() != null) {
            product.setBasePrice(dto.basePrice());
        }

        if (dto.description() != null) {
            product.setDescription(dto.description());
        }

        if (dto.availability() != null) {
            product.setAvailability(dto.availability());
        }

        Products updatedProduct = repository.save(product);

        return ProductsToDto(updatedProduct);
    }

    private void saveOrUpdateProduct(String category, String name, BigDecimal basePrice, String description, String availability) {
        // Try to find existing product by name
        List<Products> existingProducts = repository.findAll().stream()
                .filter(p -> p.getName().equals(name) && p.getCategory().equals(category))
                .collect(Collectors.toList());
        
        Products product;
        if (!existingProducts.isEmpty()) {
            // Update existing product
            product = existingProducts.get(0);
            product.setCategory(category);
            product.setName(name);
            product.setBasePrice(basePrice);
            product.setDescription(description);
            product.setAvailability(availability);
            System.out.println("Updated product: " + name);
        } else {
            // Create new product
            product = new Products(category, name, basePrice, description, availability);
            System.out.println("Created product: " + name);
        }
        repository.save(product);
    }

    public void initializeTable() {
        System.out.println("--- Starting Products table initialization/update ---");
        
        // COFFEE
        saveOrUpdateProduct("COFFEE", "Java House Espresso", new BigDecimal("3.00"),
                "A rich, single-origin shot, perfectly pulled. Bold and balanced.", "IN_STOCK");
        saveOrUpdateProduct("COFFEE", "Coffee Misto", new BigDecimal("4.00"),
                "A soothing blend of filtered house coffee and steamed milk. Simple and comforting.", "IN_STOCK");
        saveOrUpdateProduct("COFFEE", "Cappuccino", new BigDecimal("4.50"),
                "Espresso with steamed milk and a layer of velvety foam. Perfectly balanced and creamy.", "IN_STOCK");
        saveOrUpdateProduct("COFFEE", "Caramel Macchiato", new BigDecimal("5.25"),
                "Espresso with vanilla-flavored syrup, steamed milk, and caramel drizzle. Sweet and indulgent.", "IN_STOCK");
        saveOrUpdateProduct("COFFEE", "Mocha Frappuccino", new BigDecimal("6.50"),
                "Iced blended coffee drink mixed with rich chocolate syrup, milk, and ice, topped with whipped cream.", "IN_STOCK");

        // CUPCAKES
        saveOrUpdateProduct("CUPCAKES", "Vanilla Bean Bliss", new BigDecimal("4.00"),
                "Fluffy vanilla cake infused with real vanilla bean, finished with a sweet buttercream swirl.", "IN_STOCK");
        saveOrUpdateProduct("CUPCAKES", "Red Velvet Dream", new BigDecimal("4.25"),
                "Moist, ruby-red cake with a hint of cocoa, topped with classic cream cheese frosting.", "IN_STOCK");
        saveOrUpdateProduct("CUPCAKES", "Triple Chocolate Overload", new BigDecimal("4.50"),
                "Rich dark chocolate cake with chocolate chips, crowned with smooth chocolate ganache frosting.", "IN_STOCK");
        saveOrUpdateProduct("CUPCAKES", "Lemon Zest Delight", new BigDecimal("4.00"),
                "Bright and tangy lemon cake with zesty lemon frosting. Refreshing and delightful.", "IN_STOCK");
        saveOrUpdateProduct("CUPCAKES", "Strawberry Shortcake", new BigDecimal("4.25"),
                "Vanilla cake topped with fresh strawberries and whipped cream. Classic and sweet.", "IN_STOCK");

        // COOKIES
        saveOrUpdateProduct("COOKIES", "Signature Chocolate Chip", new BigDecimal("2.50"),
                "A warm, gooey classic with melted milk and dark chocolate chips.", "IN_STOCK");
        saveOrUpdateProduct("COOKIES", "Oatmeal Cranberry White Chocolate", new BigDecimal("2.75"),
                "Soft, chewy oatmeal cookie loaded with dried cranberries and white chocolate chunks.", "IN_STOCK");
        saveOrUpdateProduct("COOKIES", "Double Fudge Brownie Cookie", new BigDecimal("3.00"),
                "Rich, fudgy cookie with double the chocolate. Dense and decadent.", "IN_STOCK");
        saveOrUpdateProduct("COOKIES", "Snickerdoodle", new BigDecimal("2.50"),
                "Soft and chewy cinnamon-sugar cookie with a buttery, melt-in-your-mouth texture.", "IN_STOCK");

        // CROISSANTS
        saveOrUpdateProduct("CROISSANTS", "Classic Butter Croissant", new BigDecimal("3.75"),
                "Light, flaky, and golden-brown pastry layers, perfect served warm.", "IN_STOCK");
        saveOrUpdateProduct("CROISSANTS", "Cinnamon Swirl Croissant", new BigDecimal("4.50"),
                "Buttery croissant dough rolled with a sweet cinnamon sugar filling and finished with a light vanilla glaze.", "IN_STOCK");
        saveOrUpdateProduct("CROISSANTS", "Chocolate Almond Croissant", new BigDecimal("4.75"),
                "Buttery croissant filled with rich chocolate and topped with sliced almonds. Indulgent and satisfying.", "IN_STOCK");
        saveOrUpdateProduct("CROISSANTS", "Plain Croissant", new BigDecimal("3.50"),
                "Simple, buttery, and flaky croissant. A classic French pastry at its finest.", "IN_STOCK");
        saveOrUpdateProduct("CROISSANTS", "Ham and Cheese Croissant", new BigDecimal("5.00"),
                "Savory croissant filled with premium ham and melted cheese. Perfect for a hearty breakfast.", "IN_STOCK");

        // PASTRIES
        saveOrUpdateProduct("PASTRIES", "Cheese Danish", new BigDecimal("4.50"),
                "Flaky pastry filled with sweet cream cheese. Buttery and rich.", "IN_STOCK");
        saveOrUpdateProduct("PASTRIES", "Blueberry Muffin", new BigDecimal("3.50"),
                "Moist muffin bursting with fresh blueberries. Topped with a sweet crumb topping.", "IN_STOCK");
        saveOrUpdateProduct("PASTRIES", "Apple Turnover", new BigDecimal("4.25"),
                "Flaky pastry filled with spiced apple filling. Warm and comforting.", "IN_STOCK");
        saveOrUpdateProduct("PASTRIES", "Almond Croissant", new BigDecimal("4.75"),
                "Buttery croissant filled with almond paste and topped with sliced almonds. Rich and nutty.", "IN_STOCK");
        saveOrUpdateProduct("PASTRIES", "Chocolate Eclair", new BigDecimal("4.50"),
                "Light choux pastry filled with vanilla cream and topped with rich chocolate glaze.", "IN_STOCK");

        // SANDWICHES
        saveOrUpdateProduct("SANDWICHES", "BLT Classic", new BigDecimal("7.50"),
                "Crispy bacon, fresh lettuce, and ripe tomatoes on toasted bread. A timeless favorite.", "IN_STOCK");
        saveOrUpdateProduct("SANDWICHES", "Caprese Sandwich", new BigDecimal("8.00"),
                "Fresh mozzarella, ripe tomatoes, and basil with balsamic glaze on ciabatta. Light and fresh.", "IN_STOCK");
        saveOrUpdateProduct("SANDWICHES", "Grilled Chicken Panini", new BigDecimal("8.50"),
                "Tender grilled chicken with pesto, mozzarella, and sun-dried tomatoes on pressed ciabatta.", "IN_STOCK");
        saveOrUpdateProduct("SANDWICHES", "Turkey Avocado Club", new BigDecimal("9.00"),
                "Sliced turkey, crispy bacon, avocado, lettuce, and tomato on multigrain bread. Hearty and satisfying.", "IN_STOCK");
        saveOrUpdateProduct("SANDWICHES", "Veggie Delight", new BigDecimal("7.00"),
                "Fresh vegetables, hummus, and sprouts on whole grain bread. Healthy and delicious.", "IN_STOCK");

        // SALADS
        saveOrUpdateProduct("SALADS", "Caesar Salad", new BigDecimal("8.50"),
                "Crisp romaine lettuce with parmesan cheese, croutons, and classic Caesar dressing.", "IN_STOCK");
        saveOrUpdateProduct("SALADS", "Cobb Salad", new BigDecimal("9.50"),
                "Mixed greens with grilled chicken, bacon, hard-boiled eggs, avocado, and blue cheese. A complete meal.", "IN_STOCK");
        saveOrUpdateProduct("SALADS", "Garden Fresh Salad", new BigDecimal("7.50"),
                "Mixed greens with seasonal vegetables, cherry tomatoes, and your choice of dressing. Fresh and crisp.", "IN_STOCK");
        saveOrUpdateProduct("SALADS", "Grilled Chicken Salad", new BigDecimal("9.00"),
                "Tender grilled chicken over mixed greens with vegetables and your choice of dressing.", "IN_STOCK");
        saveOrUpdateProduct("SALADS", "Quinoa Power Bowl", new BigDecimal("9.75"),
                "Protein-packed quinoa with roasted vegetables, chickpeas, and tahini dressing. Nutritious and filling.", "IN_STOCK");

        // SMOOTHIES
        saveOrUpdateProduct("SMOOTHIES", "Berry Blast Smoothie", new BigDecimal("5.50"),
                "Mixed berries blended with yogurt and a touch of honey. Refreshing and antioxidant-rich.", "IN_STOCK");
        saveOrUpdateProduct("SMOOTHIES", "Chocolate Banana Smoothie", new BigDecimal("5.75"),
                "Rich chocolate blended with ripe bananas and milk. Creamy and indulgent.", "IN_STOCK");
        saveOrUpdateProduct("SMOOTHIES", "Green Power Smoothie", new BigDecimal("6.00"),
                "Spinach, kale, pineapple, and banana blended for a nutritious energy boost.", "IN_STOCK");
        saveOrUpdateProduct("SMOOTHIES", "Peach Mango Smoothie", new BigDecimal("5.75"),
                "Tropical peaches and mangoes blended with yogurt. Sweet and refreshing.", "IN_STOCK");
        saveOrUpdateProduct("SMOOTHIES", "Tropical Paradise Smoothie", new BigDecimal("6.25"),
                "Pineapple, mango, coconut, and banana blended for a taste of the tropics.", "IN_STOCK");

        System.out.println("--- Products table successfully initialized/updated with all menu items. ---");
    }
}
