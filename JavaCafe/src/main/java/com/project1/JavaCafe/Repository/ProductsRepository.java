package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, String> {
    //public void initializeTable();

}
