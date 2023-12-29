package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.config.jwt.JwtProvider;
import com.example.lamlaisecurity.dto.request.PaymentAccountRequest;
import com.example.lamlaisecurity.dto.response.PaymentAccountResponse;
import com.example.lamlaisecurity.entity.PaymentAccount;
import com.example.lamlaisecurity.entity.User;
import com.example.lamlaisecurity.repository.PaymentAccountRepository;
import com.example.lamlaisecurity.repository.UserRepository;
import com.example.lamlaisecurity.service.design.PaymentAccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PaymentAccountServiceImpl implements PaymentAccountService {
    @Autowired
    private PaymentAccountRepository paymentAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<PaymentAccountResponse> findAllByUser(String token) {
        User user = getUserByToken(token);
        List<PaymentAccount> paymentAccounts = paymentAccountRepository.findAllByUser(user);

        if (paymentAccounts == null || paymentAccounts.isEmpty()) {
            throw new AppException("Chưa có tài khoản thanh toán", HttpStatus.BAD_REQUEST.value());
        }

        return paymentAccounts.stream().map(item -> PaymentAccountResponse.builder()
                .cardNumber(generateResponseCardNumber(item.getCardNumber()))
                .balance(item.getBalance())
                .build()).toList();
    }

    @Override
    public PaymentAccountResponse save(PaymentAccountRequest accountRequest, String token) {
        User user = getUserByToken(token);

        String cardNumber = accountRequest.getCardNumber();
        String pin = accountRequest.getPin();

        if (!checkAccount(cardNumber, pin)) {
            return null;
        }

        if (paymentAccountRepository.existsByCardNumberAndUser(cardNumber, user)) {
            throw new AppException("Tài khoản thanh toán đã tồn tại", HttpStatus.BAD_REQUEST.value());
        }

        if (accountRequest.getBalance() < 0) {
            throw new AppException("Số tiền nhập vào không được nhỏ hơn 0", HttpStatus.BAD_REQUEST.value());
        }

        PaymentAccount paymentAccount = PaymentAccount.builder()
                .cardNumber(accountRequest.getCardNumber())
                .pin(BCrypt.hashpw(pin, BCrypt.gensalt(12)))
                .balance(accountRequest.getBalance())
                .user(user)
                .timeStamp(new Date())
                .build();

        PaymentAccount newPaymentAccount = paymentAccountRepository.save(paymentAccount);
        String responseCardNumber = generateResponseCardNumber(cardNumber);

        return PaymentAccountResponse.builder()
                .paymentAccountId(newPaymentAccount.getPaymentAccountId())
                .cardNumber(responseCardNumber)
                .balance(paymentAccount.getBalance())
                .build();
    }

    @Override
    public PaymentAccountResponse charge(Long cardId, PaymentAccountRequest accountRequest) {
        PaymentAccount paymentAccount = paymentAccountRepository.findById(cardId)
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản thanh toán", HttpStatus.BAD_REQUEST.value()));

        String encodePin = paymentAccount.getPin();
        String rawPin = accountRequest.getPin();

        if (!isInteger(rawPin)) {
            throw new AppException("Mã pin cần là số nguyên với 6 số", HttpStatus.BAD_REQUEST.value());
        }


        if (!BCrypt.checkpw(rawPin, encodePin)) {
            throw new AppException("Mã pin không chính xác", HttpStatus.BAD_REQUEST.value());
        }

        paymentAccount.setBalance(paymentAccount.getBalance() + accountRequest.getBalance());

        paymentAccountRepository.save(paymentAccount);

        String responseCardNumber = generateResponseCardNumber(paymentAccount.getCardNumber());

        return PaymentAccountResponse.builder()
                .paymentAccountId(paymentAccount.getPaymentAccountId())
                .cardNumber(responseCardNumber)
                .balance(paymentAccount.getBalance())
                .build();
    }

    @Override
    public PaymentAccountResponse findByIdAndUser(String token, Long cardId, String cardPin) {
        User user = getUserByToken(token);
        PaymentAccount paymentAccount = paymentAccountRepository.findByPaymentAccountIdAndUser(cardId, user)
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản thanh toán", HttpStatus.BAD_REQUEST.value()));

        String encodePin = paymentAccount.getPin();

        if (!isInteger(cardPin)) {
            throw new AppException("Mã pin cần là số nguyên với 6 số", HttpStatus.BAD_REQUEST.value());
        }


        if (!BCrypt.checkpw(cardPin, encodePin)) {
            throw new AppException("Mã pin không chính xác", HttpStatus.BAD_REQUEST.value());
        }

        String responseCardNumber = generateResponseCardNumber(paymentAccount.getCardNumber());

        return PaymentAccountResponse.builder()
                .paymentAccountId(paymentAccount.getPaymentAccountId())
                .cardNumber(responseCardNumber)
                .balance(paymentAccount.getBalance())
                .build();
    }

    @Override
    public void deleteById(String token, Long cardId, String password) {
        User user = getUserByToken(token);

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException("Mật khẩu không chính xác", HttpStatus.BAD_REQUEST.value());
        }

        PaymentAccount paymentAccount = paymentAccountRepository
                .findByPaymentAccountIdAndUser(cardId, user)
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản thanh toán", HttpStatus.BAD_REQUEST.value()));

        paymentAccountRepository.delete(paymentAccount);
    }

    public User getUserByToken(String token) {
        String username = jwtProvider.getUsernameByToken(token);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED.value()));
    }

    public String generateResponseCardNumber(String cardNumber) {
        System.out.println(cardNumber);
        return "***-***-" + cardNumber.substring(6);
    }

    public Boolean checkAccount(String cardNumber, String pin) {
        if (!isInteger(cardNumber) || cardNumber.length() != 9) {
            throw new AppException("Số tài khoản cần là số nguyên với 9 số", HttpStatus.BAD_REQUEST.value());
        } else if (!isInteger(pin) || pin.length() != 6) {
            throw new AppException("Mã pin cần là số nguyên với 6 số", HttpStatus.BAD_REQUEST.value());
        }

        return true;
    }

    public Boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
