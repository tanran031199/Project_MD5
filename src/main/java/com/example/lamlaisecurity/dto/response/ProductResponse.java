package com.example.lamlaisecurity.dto.response;

import com.example.lamlaisecurity.entity.Product;
import com.example.lamlaisecurity.entity.ProductImage;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponse {
    private Product product;
    private List<ProductImage> productImages;
}
