package com.example.lamlaisecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentAccountRequest {
    @NotBlank(message = "Mã số thẻ không được để trống")
    @Pattern(message = "Mã số thẻ phải là số nguyên với 9 ký tự số",
            regexp = "^[0-9]{9}$")
    private String cardNumber;
    @NotBlank(message = "Mã pin không được để trống")
    @Pattern(message = "mã pin phải là số nguyên với 6 ký tự số",
            regexp = "^[0-9]{6}$")
    private String pin;
    private Double balance = 0.0;
}
