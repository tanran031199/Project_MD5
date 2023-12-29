package com.example.lamlaisecurity.dto.response;

import com.example.lamlaisecurity.config.constant.OrderStatus;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponseAdmin {
    private Long orderId;
    private Double totalAmount;
    private String recipientName;
    private String note;
    private String receiveAddress;
    private String receivePhone;
    private OrderStatus orderStatus;
    private Date timeStamp;
}
