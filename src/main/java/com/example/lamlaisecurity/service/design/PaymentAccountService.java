package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.dto.request.PaymentAccountRequest;
import com.example.lamlaisecurity.dto.response.PaymentAccountResponse;

import java.util.List;

public interface PaymentAccountService {
    PaymentAccountResponse save(PaymentAccountRequest paymentAccountRequest, String token);

    PaymentAccountResponse findByIdAndUser(String token, Long cardId, String cardPin);

    PaymentAccountResponse charge(Long cardId, PaymentAccountRequest accountRequest);

    List<PaymentAccountResponse> findAllByUser(String token);

    void deleteById(String token, Long cardId, String password);
}
