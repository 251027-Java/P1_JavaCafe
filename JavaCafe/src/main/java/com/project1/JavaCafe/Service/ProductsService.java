package com.project1.JavaCafe.Service;

import com.project1.JavaCafe.DTO.ProductsWOIDDTO;
import com.project1.JavaCafe.Model.Products;
//import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.Repository.ProductsRepository;
import org.springframework.stereotype.Service;

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
