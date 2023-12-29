package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.config.jwt.JwtProvider;
import com.example.lamlaisecurity.entity.*;
import com.example.lamlaisecurity.repository.CartItemRepository;
import com.example.lamlaisecurity.repository.OrderDetailRepository;
import com.example.lamlaisecurity.repository.ProductRepository;
import com.example.lamlaisecurity.repository.UserRepository;
import com.example.lamlaisecurity.service.design.OrderDetailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public List<OrderDetail> saveAll(Order order, String token) {
        User user = getUserByToken(token);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            OrderDetail orderDetail = new OrderDetail();

            int quantity = cartItem.getQuantity();
            int stock = product.getStock();
            double totalPrice = cartItem.getTotalPrice();

            orderDetail.setQuantity(quantity);
            orderDetail.setTotalPrice(totalPrice);
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setBoughtPrice(product.getExportPrice());

            product.setStock(stock - quantity);
            productRepository.save(product);
            orderDetails.add(orderDetail);
        }

        cartItemRepository.deleteAllByUser(user);
        return orderDetailRepository.saveAll(orderDetails);
    }

    private User getUserByToken(String token) {
        String username = jwtProvider.getUsernameByToken(token);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED.value()));
    }
}
