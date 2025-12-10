package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerOrdersRepository extends JpaRepository<CustomerOrders, String> {
    //get user id to create new order
    // This query loads the order, joins the user for the check,
    // AND LEFT JOIN FETCHES the orderItems to populate the list efficiently.
    //@Query("SELECT o FROM CustomerOrders o JOIN FETCH o.user u LEFT JOIN FETCH o.orderItems oi WHERE o.orderId = :orderId AND u.userId = :userId")
    Optional<CustomerOrders> findByOrderIdAndUser_UserId(Integer orderId, Long userId);


}
