package com.example.lamlaisecurity.api.admin;

import com.example.lamlaisecurity.dto.response.OrderResponseAdmin;
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
@RequestMapping("/api/v1/admin/order")
public class AdminOrderApi {
    @Autowired
    private OrderService orderService;

    @GetMapping
    private ResponseEntity<?> getAll(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "orderId") String sortBy,
            @RequestParam(name = "orderBy", required = false, defaultValue = "asc") String orderBy
    ) {
        Pageable pageable;
        Page<OrderResponseAdmin> orderResponseAdmins;

        if (orderBy.equals("asc")) {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        }

        if (search == null) {
            orderResponseAdmins = orderService.findAll(pageable);
        } else {
            orderResponseAdmins = orderService.findAllByProductName(pageable, search);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orders", orderResponseAdmins.getContent());
        data.put("pageSize", orderResponseAdmins.getSize());
        data.put("currentPage", orderResponseAdmins.getNumber());
        data.put("totalElement", orderResponseAdmins.getTotalElements());
        data.put("totalPage", orderResponseAdmins.getTotalPages());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getDetail(
            @PathVariable Long orderId
    ) {
        Order order = orderService.findById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(
            @PathVariable String status
    ) {
        List<OrderResponseAdmin> orderResponseAdmins = orderService.findAllByStatus(status);
        return new ResponseEntity<>(orderResponseAdmins, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/{status}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long orderId,
            @PathVariable String status
    ) {
        Order order = orderService.updateStatus(orderId, status);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
