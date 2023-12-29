package com.example.lamlaisecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "payment_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentAccountId;
    @Column(nullable = false)
    private String cardNumber;
    @JsonIgnore
    @Column(nullable = false)
    private String pin;
    @Column(nullable = false)
    private Double balance = 0.0;
    @Column(nullable = false)
    private Date timeStamp;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @JsonIgnore
    private User user;
}
