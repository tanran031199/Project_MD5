package com.example.lamlaisecurity.dto.response;

import com.example.lamlaisecurity.config.constant.UserStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {
    private String fullName;
    private String email;
    private UserStatus status;
    private String token;
    private String tokenType = "Bearer";
}
