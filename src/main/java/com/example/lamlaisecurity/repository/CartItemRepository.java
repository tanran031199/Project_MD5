package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.CartItem;
import com.example.lamlaisecurity.entity.Product;
import com.example.lamlaisecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    void deleteAllByUser(User user);
}
