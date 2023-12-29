package com.example.lamlaisecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegisterRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;
    @NotBlank(message = "Email không được để trống")
    @Pattern(message = "Định dạng email không chính xác",
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(message = "Mật khẩu cần có ít nhất 8 ký tự hoa, thường, số, và 1 ký tự @#$%^&+=!",
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")
    private String password;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(message = "Số điện thoại không chính xác",
            regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String phoneNumber;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
