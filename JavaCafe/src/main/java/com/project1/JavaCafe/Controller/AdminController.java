package com.project1.JavaCafe.Controller;

import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.Service.ProductsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    //Products
    //PATCH mapping so they can modify availability

    // Fields
    private final ProductsService Pservice;

    // Constructor
    public AdminController(ProductsService Pservice) {
        this.Pservice = Pservice;
    }

    @GetMapping
    public ResponseEntity<List<ProductsDTO>> getAllProducts() {

        List<ProductsDTO> productsList = Pservice.getAllProducts();

        // Return status 200 OK along with the list of products
        return new ResponseEntity<>(productsList, HttpStatus.OK);
    }

    @PatchMapping("/{id}") // Assuming you are using PATCH for partial updates
    // Use ResponseEntity to explicitly set the HTTP status code
    public ResponseEntity<ProductsDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductsDTO dto
    ) {
        // 1. Call the service layer method
        ProductsDTO updatedProduct = Pservice.update(id, dto);

        // 2. Return the updated DTO with a 200 OK status.
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);

        // Alternatively, if you don't need to return the body, you could use:
        // return new ResponseEntity<>(HttpStatus.NO_CONTENT); // This returns 204 No Content
    }





    //CustomerOrders
    //PATCH mapping so they can modify status
    //SaleSummary
    //get daily summary of sales
}
