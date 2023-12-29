package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.constant.OrderStatus;
import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.config.jwt.JwtProvider;
import com.example.lamlaisecurity.dto.request.CheckoutRequest;
import com.example.lamlaisecurity.entity.*;
import com.example.lamlaisecurity.repository.*;
import com.example.lamlaisecurity.service.design.CartItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentAccountRepository paymentAccountRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public CartItem addToCart(CartItem cartItem, String token) {
        Product product = productRepository.findById(cartItem.getProduct().getProductId())
                .orElseThrow(() -> new AppException("Không tìm thấy sản phẩm!", HttpStatus.NOT_FOUND.value()));

        User user = getUserByToken(token);
        CartItem existsCartItem = null;

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if(cartItems != null && !cartItems.isEmpty()) {
            for (CartItem item : cartItems) {
                Product cartItemProduct = item.getProduct();

                if(product.getProductId().equals(cartItemProduct.getProductId())) {
                    existsCartItem = item;
                    break;
                }
            }
        }

        double pricePerUnit = product.getExportPrice();
        int stock = product.getStock();
        int quantity = cartItem.getQuantity();

        if (!checkQuantity(quantity, stock)) {
            return null;
        }

        if(existsCartItem != null) {
            int oldQuantity = existsCartItem.getQuantity();
            int newQuantity = oldQuantity + cartItem.getQuantity();

            if(!checkQuantity(newQuantity, stock)) {
                return null;
            }

            existsCartItem.setQuantity(newQuantity);
            return cartItemRepository.save(existsCartItem);
        }

        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setTotalPrice(cartItem.getQuantity() * pricePerUnit);

        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem update(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException("Không tìm thấy sản phẩm trong giỏ hàng của bạn", HttpStatus.BAD_REQUEST.value()));

        Product product = cartItem.getProduct();

        int stock = product.getStock();

        if (!checkQuantity(quantity, stock)) {
            return null;
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(quantity * product.getExportPrice());

        return cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new AppException("Không tìm thấy sản phẩm trong giỏ hàng của bạn", HttpStatus.BAD_REQUEST.value());
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void deleteAllByUser(String token) {
        User user = getUserByToken(token);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new AppException("Không có sản phẩm nào trong giỏ hàng của bạn", HttpStatus.BAD_REQUEST.value());
        }
        cartItemRepository.deleteAllByUser(user);
    }

    @Override
    public Map<String, Object> getAllByUser(String token) {
        User user = getUserByToken(token);
        Double totalAmount = 0.0;
        Map<String, Object> data = new HashMap<>();

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new AppException("Không có sản phẩm nào trong giỏ hàng của bạn", HttpStatus.BAD_REQUEST.value());
        }

        for (CartItem cartItem : cartItems) {
            totalAmount += cartItem.getTotalPrice();
        }

        data.put("cartItems", cartItems);
        data.put("totalAmount", totalAmount);

        return data;
    }

    @Override
    public Order checkout(CheckoutRequest checkoutRequest, Long cardId, String token) {
        User user = getUserByToken(token);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if(cartItems == null || cartItems.isEmpty()) {
            throw new AppException("Không có sản phẩm nào trong giỏ hàng", HttpStatus.BAD_REQUEST.value());
        }

        PaymentAccount paymentAccount = paymentAccountRepository.findById(cardId)
                .orElseThrow(() -> new AppException("Chưa có tài khoản thanh toán", HttpStatus.BAD_REQUEST.value()));

        String rawPin = checkoutRequest.getCardPin();
        String encodePin = paymentAccount.getPin();

        if(!isInteger(rawPin)) {
            throw new AppException("Mã pin cần là số nguyên với 6 số", HttpStatus.BAD_REQUEST.value());
        }

        if(!BCrypt.checkpw(rawPin, encodePin)) {
            throw new AppException("Mã pin không chính xác", HttpStatus.BAD_REQUEST.value());
        }

        double balance = paymentAccount.getBalance();
        double totalAmount = getTotalAmount(cartItems, balance);

        paymentAccount.setBalance(balance - totalAmount);
        paymentAccountRepository.save(paymentAccount);

        Order order =  Order.builder()
                .totalAmount(totalAmount)
                .recipientName(checkoutRequest.getRecipientName())
                .note(checkoutRequest.getNote())
                .receiveAddress(checkoutRequest.getReceiveAddress())
                .receivePhone(checkoutRequest.getReceivePhone())
                .orderStatus(OrderStatus.WAITING_CONFIRM)
                .paymentAccount(paymentAccount)
                .timeStamp(new Date())
                .isDelete(false)
                .user(user)
                .build();

        return orderRepository.save(order);
    }

    private Double getTotalAmount(List<CartItem> cartItems, double balance) {
        double totalAmount = 0.0;

        for (CartItem cartItem : cartItems) {
            totalAmount += cartItem.getTotalPrice();

            Product product = cartItem.getProduct();

            int quantity = cartItem.getQuantity();
            int stock = product.getStock();

            if(quantity > stock) {
                throw new AppException("Sản phẩm " + product.getProductName() + " không đủ số lượng trong kho",
                        HttpStatus.BAD_REQUEST.value());
            }
        }

        if(balance < totalAmount) {
            throw new AppException("Không đủ tiền trong tài khoản (số dư: " + balance + ", Tổng tiền: " + totalAmount + ")",
                    HttpStatus.BAD_REQUEST.value());
        }

        return totalAmount;
    }

    private User getUserByToken(String token) {
        String username = jwtProvider.getUsernameByToken(token);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED.value()));
    }

    private Boolean checkQuantity(Integer quantity, Integer stock) {
        if (quantity <= 0) {
            throw new AppException("Số lượng sản phẩm không được ít hơn 1", HttpStatus.BAD_REQUEST.value());
        } else if (quantity > stock) {
            throw new AppException("Số lượng sản phẩm không được nhiều hơn số lượng hàng có sẵn", HttpStatus.BAD_REQUEST.value());
        }

        return true;
    }

    private Boolean isInteger(String number) {
        try {
            if(number.length() != 6) {
                return false;
            }

            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
