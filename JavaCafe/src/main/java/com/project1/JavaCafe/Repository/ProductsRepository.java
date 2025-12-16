package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    // Returns the entire Products entity when searching by name
    //public Products findByName(String name);

    // need the ID for this
    //public Products findProductIdByName(String name);

    List<Products> findByCategory(String categoryName);
}
