package com.example.lamlaisecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;
    @Check(constraints = "quantity > 0")
    private Integer quantity;
    @Column(nullable = false)
    @Check(constraints = "bought_price > 0")
    private Double boughtPrice;
    @Column(nullable = false)
    @Check(constraints = "total_price > 0")
    private Double totalPrice;
    @Column(nullable = false)
    private Date timeStamp = new Date();

    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    @JsonIgnore
    private Order order;
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;
}
