package com.example.lamlaisecurity.dto.response;

import com.example.lamlaisecurity.config.constant.UserStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private UserStatus status;
}
