package com.example.lamlaisecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountRequest {
    private String fullName;
    private String password;
    private String phoneNumber;
    private String address;
}
