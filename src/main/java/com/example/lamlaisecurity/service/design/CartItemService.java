package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.dto.request.CheckoutRequest;
import com.example.lamlaisecurity.entity.CartItem;
import com.example.lamlaisecurity.entity.Order;

import java.util.List;
import java.util.Map;

public interface CartItemService {
    CartItem addToCart(CartItem cart, String token);

    Map<String, Object> getAllByUser(String token);

    CartItem update(Long cartItemId, Integer quantity);

    void deleteCartItem(Long cartItemId);

    void deleteAllByUser(String token);

    Order checkout(CheckoutRequest checkoutRequest, Long cardId, String token);
}
