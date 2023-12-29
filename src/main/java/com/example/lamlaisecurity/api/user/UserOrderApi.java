package com.example.lamlaisecurity.api.user;

import com.example.lamlaisecurity.entity.Order;
import com.example.lamlaisecurity.service.design.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/order")
public class UserOrderApi {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "orderId") String sortBy,
            @RequestParam(name = "orderBy", required = false, defaultValue = "asc") String orderBy
    ) {
        Pageable pageable;
        Page<Order> orderPage;

        if (orderBy.equals("asc")) {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        }

        if (search == null) {
            orderPage = orderService.findAllByUser(pageable, token.substring(7));
        } else {
            orderPage = orderService.findAllByUserAndProductName(pageable, search, token.substring(7));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orders", orderPage.getContent());
        data.put("pageSize", orderPage.getSize());
        data.put("currentPage", orderPage.getNumber());
        data.put("totalElement", orderPage.getTotalElements());
        data.put("totalPage", orderPage.getTotalPages());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/detail/{orderId}")
    public ResponseEntity<?> getDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId
    ) {
        Order order = orderService.findByIdAndUser(orderId, token.substring(7));
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/{orderStatus}")
    public ResponseEntity<?> getAllByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String orderStatus
    ) {
        List<Order> orders = orderService.findAllByUserAndStatus(orderStatus, token.substring(7));
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancel(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId
    ) {
        orderService.cancelById(token.substring(7), orderId);
        return new ResponseEntity<>("Hủy thành công", HttpStatus.OK);
    }
}
