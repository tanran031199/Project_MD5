package com.example.lamlaisecurity.api.user;

import com.example.lamlaisecurity.dto.request.AccountRequest;
import com.example.lamlaisecurity.dto.request.RegisterRequest;
import com.example.lamlaisecurity.dto.response.AccountResponse;
import com.example.lamlaisecurity.entity.User;
import com.example.lamlaisecurity.service.design.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/account")
public class AccountApi {
    @Autowired
    private UserService userService;

    @GetMapping
    private ResponseEntity<?> getMyAccount(
            @RequestHeader("Authorization") String token
    ) {
        User user = userService.findByToken(token.substring(7));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    private ResponseEntity<?> editAccount(
            @RequestHeader("Authorization") String token,
            @RequestBody AccountRequest accountRequest
            ) {
        AccountResponse accountResponse = userService.editAccount(token.substring(7), accountRequest);
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }
}
