package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.DTO.SalesAggregationDTO;
import com.project1.JavaCafe.DTO.SalesSummaryDTO;
import com.project1.JavaCafe.Model.CustomerOrders;
import com.project1.JavaCafe.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerOrdersRepository extends JpaRepository<CustomerOrders, Integer> {
    //get user id to create new order
    // This query loads the order, joins the user for the check,
    // AND LEFT JOIN FETCHES the orderItems to populate the list efficiently.
    //@Query("SELECT o FROM CustomerOrders o JOIN FETCH o.user u LEFT JOIN FETCH o.orderItems oi WHERE o.orderId = :orderId AND u.userId = :userId")
    Optional<CustomerOrders> findByOrderIdAndUser_UserId(Integer orderId, Long userId);
    List<CustomerOrders> findByStatusIn(List<String> statuses);
    // The JPQL uses the constructor mapping (new ...) to populate the DTO.
    // Replace the example fields (totalPrice) with your actual CustomerOrders fields.
    @Query("SELECT new com.project1.JavaCafe.DTO.SalesAggregationDTO(" +
            "  CAST(COUNT(DISTINCT o.orderId) AS integer), " + // Cast the COUNT result to INTEGER
            "  CAST(SUM(oi.quantity) AS integer) " +  // Sum the quantity from OrderItems
            ") FROM CustomerOrders o JOIN o.orderItems oi") // Join the OrderItems collection
    SalesAggregationDTO calculateAllTimeAggregates();

}
