package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.dto.response.ProductResponse;
import com.example.lamlaisecurity.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductResponse> findAll(Pageable pageable);

    ProductResponse findById(Long productId);

    Product save(Product product);

    void deleteById(Long productId);

    Page<ProductResponse> findAllByCategory(Pageable pageable, Long cateId);

    Page<ProductResponse> findAllByName(Pageable pageable, String search);

    Page<ProductResponse> findAllByNameAndCategory(Pageable pageable, Long cateId, String search);

    List<ProductResponse> findAllNewProduct();
}
