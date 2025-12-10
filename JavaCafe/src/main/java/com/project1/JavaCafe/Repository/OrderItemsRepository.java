package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository<OrderItems, String> {
}
