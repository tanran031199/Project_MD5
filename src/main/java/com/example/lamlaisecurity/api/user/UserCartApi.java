package com.example.lamlaisecurity.api.user;

import com.example.lamlaisecurity.dto.request.CheckoutRequest;
import com.example.lamlaisecurity.entity.CartItem;
import com.example.lamlaisecurity.entity.Order;
import com.example.lamlaisecurity.service.design.CartItemService;
import com.example.lamlaisecurity.service.design.OrderDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/cart")
public class UserCartApi {
    @Autowired
    private CartItemService cartService;
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestHeader("Authorization") String token
    ) {
        Map<String, Object> cartItems = cartService.getAllByUser(token.substring(7));
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(
            @RequestHeader("Authorization") String token,
            @RequestBody CartItem cartItem) {
        CartItem newCartItem = cartService.addToCart(cartItem, token.substring(7));
        return new ResponseEntity<>(newCartItem, HttpStatus.OK);
    }

    @PostMapping("checkout/{cardId}")
    public ResponseEntity<String> checkout(
            @RequestHeader("Authorization") String token,
            @PathVariable Long cardId,
            @Valid @RequestBody CheckoutRequest checkoutRequest
    ) {
        Order order = cartService.checkout(checkoutRequest, cardId, token.substring(7));
        orderDetailService.saveAll(order, token.substring(7));
        return new ResponseEntity<>("Thanh toán thành công", HttpStatus.OK);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody Integer quantity
    ) {
        CartItem cartItem = cartService.update(cartItemId, quantity);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(
            @PathVariable Long cartItemId
    ) {
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>("Gỡ sản phẩm khỏi giỏ hàng thành công", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCartByUser(
            @RequestHeader("Authorization") String token
    ) {
        cartService.deleteAllByUser(token.substring(7));
        return new ResponseEntity<>("Gỡ bỏ tất cả sản phẩm khỏi giỏ hàng thành công", HttpStatus.OK);
    }
}
