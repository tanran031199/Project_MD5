package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.dto.response.ProductResponse;
import com.example.lamlaisecurity.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByIsDelete(Pageable pageable, Boolean isDelete);

    Page<Product> findAllByIsDeleteAndProductNameContainingIgnoreCase(Pageable pageable, Boolean isDelete, String name);

    Optional<Product> findByProductIdAndIsDelete(Long productId, Boolean isDelete);

    @Query("select p from Product p " +
            "join Category c on c.categoryId = p.category.categoryId " +
            "where (c.categoryId = :cateId or c.parent.categoryId = :cateId) " +
            "and p.isDelete = :isDelete " +
            "and c.isDelete = :isDelete")
    Page<Product> findAllByIsDelete(Pageable pageable, @Param("cateId") Long cateId, @Param("isDelete") Boolean isDelete);

    @Query("select p from Product p " +
            "join Category c on c.categoryId = p.category.categoryId " +
            "where (c.categoryId = :cateId or c.parent.categoryId = :cateId) " +
            "and p.isDelete = :isDelete " +
            "and c.isDelete = :isDelete " +
            "and p.productName like %:name%")
    Page<Product> findAllByIsDeleteAndProductNameContainingIgnoreCase(Pageable pageable, @Param("cateId") Long cateId, @Param("name") String name, @Param("isDelete") Boolean isDelete);

    List<Product> findAllByIsDeleteFalseOrderByTimeStampDesc();
}
