package com.project1.JavaCafe.Repository;

import com.project1.JavaCafe.Model.CustomerOrders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerOrdersRepository extends JpaRepository<CustomerOrders, Integer> {

    Optional<CustomerOrders> findByOrderIdAndUser_UserId(Integer orderId, Long userId);
    List<CustomerOrders> findByStatusIn(List<String> statuses);


}
