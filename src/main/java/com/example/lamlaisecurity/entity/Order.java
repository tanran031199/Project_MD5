package com.example.lamlaisecurity.entity;

import com.example.lamlaisecurity.config.constant.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column(nullable = false)
    @Check(name = "total_amount_check", constraints = "total_amount > 0")
    private Double totalAmount;
    @Column(nullable = false)
    private String recipientName;
    private String note;
    @Column(nullable = false)
    private String receiveAddress;
    @Column(nullable = false)
    private String receivePhone;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(nullable = false)
    private Date timeStamp = new Date();
    @Column(nullable = false)
    @JsonIgnore
    private Boolean isDelete = false;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "paymentAccountId", referencedColumnName = "paymentAccountId", nullable = false)
    private PaymentAccount paymentAccount;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;
}
