package com.example.lamlaisecurity.api.admin;

import com.example.lamlaisecurity.dto.response.AccountResponse;
import com.example.lamlaisecurity.entity.Category;
import com.example.lamlaisecurity.entity.User;
import com.example.lamlaisecurity.repository.UserRepository;
import com.example.lamlaisecurity.service.design.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/user")
public class AdminUserApi {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "userId") String sortBy,
            @RequestParam(name = "orderBy", required = false, defaultValue = "asc") String orderBy
    ) {
        Pageable pageable;
        Page<AccountResponse> userPage;

        if (orderBy.equals("asc")) {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        }

        if (search == null) {
            userPage = userService.findAll(pageable);
        } else {
            userPage = userService.findAllByName(pageable, search);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("users", userPage.getContent());
        data.put("pageSize", userPage.getSize());
        data.put("currentPage", userPage.getNumber());
        data.put("totalElement", userPage.getTotalElements());
        data.put("totalPage", userPage.getTotalPages());

        if (userPage.isEmpty()) {
            return new ResponseEntity<>("Chưa có người dùng nào", HttpStatus.OK);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PutMapping("/lock/{userId}")
    public ResponseEntity<?> lockUser(
            @PathVariable Long userId
    ) {
        userService.lockUser(userId);
        return new ResponseEntity<>("Khóa tài khoản thành công", HttpStatus.OK);
    }

    @PutMapping("/unlock/{userId}")
    public ResponseEntity<?> unlock(
            @PathVariable Long userId
    ) {
        userService.unlock(userId);
        return new ResponseEntity<>("Mở khóa tài khoản thành công", HttpStatus.OK);
    }
}
