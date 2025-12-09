package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrdersRepository extends JpaRepository<CustomerOrders, String> {
    //get user id to create new order


}
