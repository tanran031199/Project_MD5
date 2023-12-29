package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.dto.request.AccountRequest;
import com.example.lamlaisecurity.dto.request.LoginRequest;
import com.example.lamlaisecurity.dto.request.RegisterRequest;
import com.example.lamlaisecurity.dto.response.AccountResponse;
import com.example.lamlaisecurity.dto.response.LoginResponse;
import com.example.lamlaisecurity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);

    User findByToken(String substring);

    AccountResponse editAccount(String substring, AccountRequest accountRequest);

    Page<AccountResponse> findAll(Pageable pageable);

    Page<AccountResponse> findAllByName(Pageable pageable, String search);

    void lockUser(Long userId);

    void unlock(Long userId);
}
