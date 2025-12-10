package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.ProductsWOIDDTO;
import com.project1.JavaCafe.Model.Products;
//import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.Repository.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductsService {
    // Fields
    private final ProductsRepository repository;

    // Constructor
    public ProductsService (ProductsRepository repository){
        this.repository = repository;
    }

    // Methods



    public ProductsDTO create(ProductsWOIDDTO dto) {
        Products product = new Products(dto.category(), dto.name(), dto.basePrice(), dto.description(), dto.availability());
        return ProductsToDto(repository.save(product));
    }

    private ProductsDTO ProductsToDto(Products product) {
        return new ProductsDTO(
                product.getProductId(),   // 1
                product.getCategory(),    // 2
                product.getName(),        // 3
                product.getBasePrice(),   // 4
                product.getDescription(), // 5
                product.getAvailability() // 6
        );
    }

    public List<ProductsDTO> getAllProducts() {
        // the repo method returns a list of expenses...
        // we need to convert every expense on the list to a DTO...
        // keep/put back in a list to return
        return repository.findAll().stream().map(this::ProductsToDto).toList();
    }

    /*
        Long productId,
        String category,
        String name,
        BigDecimal basePrice,
        String description,
        String availability
     */

    public ProductsDTO update(Long id, ProductsDTO dto) {

        // 1. Find the existing product entity by ID. Throws 404 if not found.
        Products product = repository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id)
                );

        // 2. Update the fields of the retrieved 'product' entity using the 'dto' fields

        // Check if the DTO field is not null before setting (optional, but good practice for PATCH-like updates)
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

        // Assuming availability is either a String or an Enum
        if (dto.availability() != null) {
            product.setAvailability(dto.availability());
        }

        // 3. Save the updated entity back to the database.
        Products updatedProduct = repository.save(product);

        // 4. Convert the saved entity back to the DTO without ID for the response.
        // NOTE: Replace 'ProductToDto' with your actual conversion method name.
        return ProductsToDto(updatedProduct);
    }

    public void initializeTable() {
        // Use count() to check if the table has any records
        if(repository.count() == 0) {

            // Create a properly initialized Products entity with necessary data
            Products espresso = new Products(
                    "COFFEE",
                    "Java House Espresso",
                    new java.math.BigDecimal("3.00"),
                    "A rich, single-origin shot, perfectly pulled. Bold and balanced.",
                    "IN_STOCK"
            );

            // Use the standard JPA save() method
            repository.save(espresso);

            Products misto = new Products(
                    "COFFEE",
                    "Coffee Misto",
                    new java.math.BigDecimal("4.00"),
                    "A soothing blend of filtered house coffee and steamed milk. Simple and comforting.",
                    "IN_STOCK"
            );
            repository.save(misto);

            Products mocha = new Products(
                    "COFFEE",
                    "Mocha Frappuccino",
                    new java.math.BigDecimal("6.50"),
                    "Iced blended coffee drink mixed with rich chocolate syrup, milk, and ice, topped with whipped cream.",
                    "IN_STOCK"
            );
            repository.save(mocha);

            Products vanilla = new Products(
                    "CUPCAKES",
                    "Vanilla Bean Bliss",
                    new java.math.BigDecimal("4.00"),
                    "Fluffy vanilla cake infused with real vanilla bean, finished with a sweet buttercream swirl.",
                    "IN_STOCK"
            );
            repository.save(vanilla);

            Products red = new Products(
                    "CUPCAKES",
                    "Red Velvet Dream",
                    new java.math.BigDecimal("4.25"),
                    "Moist, ruby-red cake with a hint of cocoa, topped with classic cream cheese frosting.",
                    "IN_STOCK"
            );
            repository.save(red);

            Products chocolate = new Products(
                    "CUPCAKES",
                    "Triple Chocolate Overload",
                    new java.math.BigDecimal("4.25"),
                    "Rich dark chocolate cake with chocolate chips, crowned with smooth chocolate ganache frosting.",
                    "IN_STOCK"
            );
            repository.save(chocolate);

            Products butter  = new Products(
                    "CROISSANTS",
                    "Classic Butter Croissant",
                    new java.math.BigDecimal("3.75"),
                    "Light, flaky, and golden-brown pastry layers, perfect served warm.",
                    "IN_STOCK"
            );
            repository.save(butter);

            Products cinnamon = new Products(
                    "CROISSANTS",
                    "Cinnamon Swirl Croissant",
                    new java.math.BigDecimal("4.50"),
                    "Buttery croissant dough rolled with a sweet cinnamon sugar filling and finished with a light vanilla glaze.",
                    "IN_STOCK"
            );
            repository.save(cinnamon);

            Products chip = new Products(
                    "COOKIES",
                    "Signature Chocolate Chip",
                    new java.math.BigDecimal("2.50"),
                    "A warm, gooey classic with melted milk and dark chocolate chips.",
                    "IN_STOCK"
            );
            repository.save(chip);

            Products oatmeal = new Products(
                    "COOKIES",
                    "Oatmeal Cranberry White Chocolate",
                    new java.math.BigDecimal("2.75"),
                    "Soft, chewy oatmeal cookie loaded with dried cranberries and white chocolate chunks.",
                    "IN_STOCK"
            );
            repository.save(oatmeal);




            System.out.println("--- Products table successfully initialized with sample data. ---");
        }
    }
}
