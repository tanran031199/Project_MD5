package com.example.lamlaisecurity.service.design;


import com.example.lamlaisecurity.dto.response.OrderResponseAdmin;
import com.example.lamlaisecurity.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Page<Order> findAllByUser(Pageable pageable, String token);

    Page<Order> findAllByUserAndProductName(Pageable pageable, String productName, String token);

    List<Order> findAllByUserAndStatus(String orderStatus, String token);

    void cancelById(String substring, Long orderId);

    Page<OrderResponseAdmin> findAll(Pageable pageable);

    Page<OrderResponseAdmin> findAllByProductName(Pageable pageable, String search);

    Order findById(Long orderId);

    List<OrderResponseAdmin> findAllByStatus(String status);

    Order updateStatus(Long orderId, String status);

    Order findByIdAndUser(Long orderId, String substring);
}
