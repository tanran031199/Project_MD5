package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.entity.User;

public interface EmailVerifyTokenService {
    String verifyToken(String token);
    Long saveUserVerificationToken(User user, String verificationToken);
}
