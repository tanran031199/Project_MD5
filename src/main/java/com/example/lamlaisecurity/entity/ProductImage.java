package com.example.lamlaisecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    @Column(nullable = false)
    private String fileName;
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    @JsonIgnore
    private Product product;
}
