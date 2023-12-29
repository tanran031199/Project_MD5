package com.example.lamlaisecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CheckoutRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;
    private String note;
    @NotBlank(message = "Địa chỉ người nhận không được để trống")
    private String receiveAddress;
    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(message = "Số điện thoại không chính xác",
            regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String receivePhone;
    @NotBlank(message = "Mã pin không được để trống")
    @Pattern(message = "mã pin phải là số nguyên với 6 ký tự số",
            regexp = "^[0-9]{6}$")
    private String cardPin;
}
