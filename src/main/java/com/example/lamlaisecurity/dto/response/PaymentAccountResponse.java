package com.example.lamlaisecurity.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentAccountResponse {
    private Long paymentAccountId;
    private String cardNumber;
    private Double balance;
}
