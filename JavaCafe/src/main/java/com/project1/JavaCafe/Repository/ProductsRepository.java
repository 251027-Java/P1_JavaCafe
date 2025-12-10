package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    // Returns the entire Products entity when searching by name
    //public Products findByName(String name);

    // (Optional: If you only need the ID, keep this, but fix the return type if it's not Integer)
    public Products findProductIdByName(String name);
}
