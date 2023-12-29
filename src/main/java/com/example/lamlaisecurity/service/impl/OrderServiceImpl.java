package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.constant.OrderStatus;
import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.config.jwt.JwtProvider;
import com.example.lamlaisecurity.dto.response.OrderResponseAdmin;
import com.example.lamlaisecurity.entity.*;
import com.example.lamlaisecurity.repository.OrderRepository;
import com.example.lamlaisecurity.repository.PaymentAccountRepository;
import com.example.lamlaisecurity.repository.ProductRepository;
import com.example.lamlaisecurity.repository.UserRepository;
import com.example.lamlaisecurity.service.design.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentAccountRepository paymentAccountRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public Page<Order> findAllByUser(Pageable pageable, String token) {
        User user = getUserByToken(token);
        return orderRepository.findAllByIsDeleteFalseAndUser(pageable, user);
    }

    @Override
    public Page<Order> findAllByUserAndProductName(Pageable pageable, String productName, String token) {
        User user = getUserByToken(token);
        return orderRepository.findAllByIsDeleteFalseAndProductNameAndUser(pageable, productName, user);
    }

    @Override
    public Page<OrderResponseAdmin> findAll(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByIsDeleteFalse(pageable);

        return orderPage.map(item -> OrderResponseAdmin.builder()
                .orderId(item.getOrderId())
                .totalAmount(item.getTotalAmount())
                .recipientName(item.getRecipientName())
                .note(item.getNote())
                .receiveAddress(item.getReceiveAddress())
                .receivePhone(item.getReceivePhone())
                .orderStatus(item.getOrderStatus())
                .timeStamp(item.getTimeStamp())
                .build());
    }

    @Override
    public Page<OrderResponseAdmin> findAllByProductName(Pageable pageable, String productName) {
        Page<Order> orderPage = orderRepository.findAllByIsDeleteFalseAndProductName(pageable, productName);

        return orderPage.map(item -> OrderResponseAdmin.builder()
                .orderId(item.getOrderId())
                .totalAmount(item.getTotalAmount())
                .recipientName(item.getRecipientName())
                .note(item.getNote())
                .receiveAddress(item.getReceiveAddress())
                .receivePhone(item.getReceivePhone())
                .orderStatus(item.getOrderStatus())
                .timeStamp(item.getTimeStamp())
                .build());
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findByOrderIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new AppException("Không tìm thấy lịch sử đặt hàng", HttpStatus.BAD_REQUEST.value()));
    }

    @Override
    public Order findByIdAndUser(Long orderId, String token) {
        User user = getUserByToken(token);
        return orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new AppException("Không tìm thấy lịch sử đặt hàng", HttpStatus.BAD_REQUEST.value()));
    }

    @Override
    public void cancelById(String token, Long orderId) {
        User user = getUserByToken(token);
        Order order = orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new AppException("Không tìm thấy lịch sử mua hàng", HttpStatus.BAD_REQUEST.value()));

        if (order.getOrderStatus().equals(OrderStatus.WAITING_CONFIRM)) {
            order.setOrderStatus(OrderStatus.USER_CANCEL);

            cancelOrder(order);
        } else {
            throw new AppException("Không thể hủy đơn đặt hàng đã được xác nhận", HttpStatus.BAD_REQUEST.value());
        }
    }

    @Override
    public Order updateStatus(Long orderId, String status) {
        Order order = findById(orderId);

        if (order.getOrderStatus().equals(OrderStatus.WAITING_CONFIRM)) {
            if (OrderStatus.CONFIRMED.name().equalsIgnoreCase(status)) {
                order.setOrderStatus(OrderStatus.CONFIRMED);
                order = orderRepository.save(order);
            } else if (OrderStatus.ADMIN_CANCEL.name().equalsIgnoreCase(status)) {
                order.setOrderStatus(OrderStatus.ADMIN_CANCEL);
                order = cancelOrder(order);
            }
        }

        return order;
    }

    private Order cancelOrder(Order order) {
        PaymentAccount paymentAccount = order.getPaymentAccount();
        List<OrderDetail> orderDetails = order.getOrderDetails();

        paymentAccount.setBalance(paymentAccount.getBalance() + order.getTotalAmount());

        for (OrderDetail od : orderDetails) {
            Product product = od.getProduct();
            int quantity = od.getQuantity();
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
        }

        paymentAccountRepository.save(paymentAccount);
        return orderRepository.save(order);
    }

    @Override
    public List<OrderResponseAdmin> findAllByStatus(String orderStatus) {
        OrderStatus enumStatus = getOrderStatus(orderStatus);

        List<Order> orders = orderRepository.findAllByIsDeleteFalseAndOrderStatus(enumStatus);

        return orders.stream().map(item -> OrderResponseAdmin.builder()
                .orderId(item.getOrderId())
                .totalAmount(item.getTotalAmount())
                .recipientName(item.getRecipientName())
                .note(item.getNote())
                .receiveAddress(item.getReceiveAddress())
                .receivePhone(item.getReceivePhone())
                .orderStatus(item.getOrderStatus())
                .timeStamp(item.getTimeStamp())
                .build()).toList();
    }

    public List<Order> findAllByUserAndStatus(String orderStatus, String token) {
        User user = getUserByToken(token);
        OrderStatus enumStatus = getOrderStatus(orderStatus);

        return orderRepository.findAllByUserAndOrderStatusAndIsDeleteFalse(user, enumStatus);
    }

    private OrderStatus getOrderStatus(String orderStatus) {
        OrderStatus enumStatus = null;

        if (OrderStatus.WAITING_CONFIRM.name().equalsIgnoreCase(orderStatus)) {
            enumStatus = OrderStatus.WAITING_CONFIRM;
        } else if (OrderStatus.CONFIRMED.name().equalsIgnoreCase(orderStatus)) {
            enumStatus = OrderStatus.CONFIRMED;
        } else if (OrderStatus.USER_CANCEL.name().equalsIgnoreCase(orderStatus)) {
            enumStatus = OrderStatus.USER_CANCEL;
        } else if (OrderStatus.ADMIN_CANCEL.name().equalsIgnoreCase(orderStatus)) {
            enumStatus = OrderStatus.ADMIN_CANCEL;
        }

        return enumStatus;
    }

    private User getUserByToken(String token) {
        String username = jwtProvider.getUsernameByToken(token);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED.value()));
    }
}
