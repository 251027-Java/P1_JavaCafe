package com.project1.JavaCafe.Controller;
import com.project1.JavaCafe.DTO.MenuDescriptionDTO;
import com.project1.JavaCafe.DTO.MenuProductsDTO;
import com.project1.JavaCafe.DTO.ProductsDTO;
import com.project1.JavaCafe.Service.ProductsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu") // domain:port/api/expenses
public class MenuController {
    // Fields
    private final ProductsService Pservice;

    // Constructor
    public MenuController(ProductsService Pservice) {
        this.Pservice = Pservice;
    }

    @GetMapping
    public ResponseEntity<List<MenuProductsDTO>> getAllProducts() {

        List<MenuProductsDTO> productsList = Pservice.getAllMenuProducts();

        // Return status 200 OK along with the list of products
        return new ResponseEntity<>(productsList, HttpStatus.OK);
    }

    @GetMapping("/description/{productId}")
    public ResponseEntity<MenuDescriptionDTO> getProductDetails(@PathVariable Long productId) {

        MenuDescriptionDTO description = Pservice.getProductDescription(productId);

        // Return status 200 OK along with the description DTO
        return new ResponseEntity<>(description, HttpStatus.OK);
    }


}
