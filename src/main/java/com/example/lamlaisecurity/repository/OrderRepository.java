package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.config.constant.OrderStatus;
import com.example.lamlaisecurity.dto.response.OrderResponseAdmin;
import com.example.lamlaisecurity.entity.Order;
import com.example.lamlaisecurity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
//    @Query("select o from Order o where o.user = :user and o.isDelete = false")
    Page<Order> findAllByIsDeleteFalseAndUser(Pageable pageable, User user);

    @Query("select o from Order o " +
            "join OrderDetail od on o = od.order " +
            "join Product p on od.product = p " +
            "where p.productName like %:productName% and o.isDelete = false and o.user = :user")
    Page<Order> findAllByIsDeleteFalseAndProductNameAndUser(Pageable pageable,
                                                            @Param("productName") String productName,
                                                            @Param("user") User user);

    List<Order> findAllByUserAndOrderStatusAndIsDeleteFalse(User user, OrderStatus orderStatus);

    Optional<Order> findByOrderIdAndUser(Long orderId, User user);

    Page<Order> findAllByIsDeleteFalse(Pageable pageable);

    @Query("select o from Order o " +
            "join OrderDetail od on o = od.order " +
            "join Product p on od.product = p " +
            "where p.productName like %:productName% and o.isDelete = false")
    Page<Order> findAllByIsDeleteFalseAndProductName(Pageable pageable, @Param("productName") String productName);

    Optional<Order> findByOrderIdAndIsDeleteFalse(Long orderId);

    List<Order> findAllByIsDeleteFalseAndOrderStatus(OrderStatus enumStatus);
}
