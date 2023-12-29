package com.example.lamlaisecurity.api.user;

import com.example.lamlaisecurity.dto.request.PaymentAccountRequest;
import com.example.lamlaisecurity.dto.response.PaymentAccountResponse;
import com.example.lamlaisecurity.service.design.PaymentAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/payment")
public class UserPaymentApi {
    @Autowired
    private PaymentAccountService paymentAccountService;

    @GetMapping
    public ResponseEntity<List<PaymentAccountResponse>> getAll(
            @RequestHeader("Authorization") String token
    ) {
        List<PaymentAccountResponse> accountResponses = paymentAccountService
                .findAllByUser(token.substring(7));

        return new ResponseEntity<>(accountResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PaymentAccountResponse> create(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody PaymentAccountRequest paymentAccountRequest
            ) {
        PaymentAccountResponse paymentAccountResponse = paymentAccountService
                .save(paymentAccountRequest, token.substring(7));

        return new ResponseEntity<>(paymentAccountResponse, HttpStatus.OK);
    }

    @PostMapping("/{cardId}")
    public ResponseEntity<PaymentAccountResponse> getDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long cardId,
            @RequestBody String cardPin
    ) {
        PaymentAccountResponse paymentAccountResponse = paymentAccountService
                .findByIdAndUser(token.substring(7), cardId, cardPin);

        return new ResponseEntity<>(paymentAccountResponse, HttpStatus.OK);
    }

    @PutMapping("/charge/{cardId}")
    public ResponseEntity<PaymentAccountResponse> charge(
            @PathVariable Long cardId,
            @RequestBody PaymentAccountRequest paymentAccountRequest
    ) {
        PaymentAccountResponse paymentAccountResponse = paymentAccountService.charge(cardId, paymentAccountRequest);
        return new ResponseEntity<>(paymentAccountResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(
            @RequestHeader("Authorization") String token,
            @PathVariable Long cardId,
            @RequestBody String password
    ) {
        paymentAccountService.deleteById(token.substring(7), cardId, password);
        return new ResponseEntity<>("Xóa tài khoản thanh toán thành công", HttpStatus.OK);
    }
}
