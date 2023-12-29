package com.example.lamlaisecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    @Column(nullable = false)
    @Check(name = "cart_quantity_check", constraints = "quantity > 0")
    private Integer quantity;
    @Column(nullable = false)
    @Check(name = "cart_total_price_check", constraints = "total_price > 0")
    private Double totalPrice;
    @Column(nullable = false)
    private Date timeStamp = new Date();

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;
}
