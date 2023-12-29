package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.entity.Order;
import com.example.lamlaisecurity.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> saveAll(Order order, String token);
}
