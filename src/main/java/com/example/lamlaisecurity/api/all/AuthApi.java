package com.example.lamlaisecurity.api.all;

import com.example.lamlaisecurity.dto.request.LoginRequest;
import com.example.lamlaisecurity.dto.request.RegisterRequest;
import com.example.lamlaisecurity.dto.response.LoginResponse;
import com.example.lamlaisecurity.entity.User;
import com.example.lamlaisecurity.event.RegistrationCompleteEvent;
import com.example.lamlaisecurity.service.design.EmailVerifyTokenService;
import com.example.lamlaisecurity.service.design.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApi {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailVerifyTokenService verifyTokenService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest,
                                           HttpServletRequest request) {

        final String applicationUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        User user = userService.register(registerRequest);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl));

        String msg = "Tạo tài khoản thành công, mã xác nhận sẽ được về email vui lòng xác nhận để sử dụng dịch vụ!";
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @GetMapping("/emailVerifyToken")
    public ResponseEntity<String> emailVerifyToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(verifyTokenService.verifyToken(token), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(userService.login(loginRequest), HttpStatus.OK);
    }
}
