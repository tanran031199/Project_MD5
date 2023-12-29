package com.example.lamlaisecurity.service.design;


import com.example.lamlaisecurity.dto.response.CategoryDetailResponse;
import com.example.lamlaisecurity.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);

    Category findById(Long cateId);

    Category save(Category category);

    CategoryDetailResponse findDetail(Long cateId);

    Integer delete(Long cateId);

    Page<Category> findAllByName(Pageable pageable, String search);
}
