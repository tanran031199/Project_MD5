package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByIsDelete(Pageable pageable, Boolean isDelete);
    Page<Category> findAllByIsDeleteAndCategoryNameContainingIgnoreCase(Pageable pageable, Boolean isDelete, String name);
    Optional<Category> findByCategoryIdAndIsDelete(Long cateId, Boolean isDelete);
    @Modifying
    @Query("update Category c set c.isDelete = true where c.categoryId = : cateId")
    Integer updateIsDeleteByCategoryId(Long cateId);

    List<Category> findAllByIsDeleteFalse();
}
