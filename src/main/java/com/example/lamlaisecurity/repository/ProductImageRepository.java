package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("select p from ProductImage p where p.product.productId = :productId")
    List<ProductImage> findAllByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("delete from ProductImage p where p.product.productId = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);
}
