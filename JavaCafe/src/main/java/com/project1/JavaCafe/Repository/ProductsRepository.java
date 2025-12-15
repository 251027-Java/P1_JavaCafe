package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {


    List<Products> findByCategory(String categoryName);
}
