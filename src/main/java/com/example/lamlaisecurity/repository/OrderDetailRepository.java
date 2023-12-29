package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
