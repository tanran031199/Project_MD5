package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.entity.EmailVerifyToken;
import com.example.lamlaisecurity.entity.User;
import com.example.lamlaisecurity.config.constant.UserStatus;
import com.example.lamlaisecurity.repository.EmailVerifyTokenRepository;
import com.example.lamlaisecurity.repository.UserRepository;
import com.example.lamlaisecurity.service.design.EmailVerifyTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class EmailVerifyTokenServiceImpl implements EmailVerifyTokenService {
    @Autowired
    private EmailVerifyTokenRepository verifyTokenRepository;
    @Autowired
    private UserRepository userRepository;
    private final int VERIFY_TOKEN_EXPIRED = (1000 * 60 * 15) + 1000;

    @Override
    public String verifyToken(String token) {
        EmailVerifyToken theToken = verifyTokenRepository.findByToken(token).orElse(null);

        final long CURRENT_TIME = System.currentTimeMillis();

        if(theToken == null) {
            throw new AppException("Mã xác nhận không hợp lệ", HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        User user = theToken.getUser();

        if(!user.getStatus().equals(UserStatus.WAITING_CONFIRM)) {
            throw new AppException("Email đã được xác thực vui lòng đăng nhập để sử dụng", HttpStatus.BAD_REQUEST.value());
        }

        if((theToken.getExpirationTime().getTime() + VERIFY_TOKEN_EXPIRED) <= CURRENT_TIME) {
            verifyTokenRepository.deleteById(theToken.getId());
            userRepository.deleteById(user.getUserId());
            throw new AppException("Mã xác nhận đã hết hạn vui lòng tạo lại tài khoản!", HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        user.setStatus(UserStatus.ENABLE);
        userRepository.save(user);

        return "Xin chào " + user.getFullName() + "! bạn đã kích hoạt tài khoản thành công," +
                " hãy đăng nhập để trải nghiệm dịch vụ của chúng tôi!";
    }

    @Override
    public Long saveUserVerificationToken(User user, String verificationToken) {
        final Date currentTime = new Date();

        EmailVerifyToken emailVerifyToken = EmailVerifyToken.builder()
                .user(user)
                .token(verificationToken)
                .expirationTime(currentTime)
                .build();

        verifyTokenRepository.save(emailVerifyToken);

        return currentTime.getTime() + VERIFY_TOKEN_EXPIRED;
    }
}
