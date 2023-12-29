package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.dto.response.CategoryDetailResponse;
import com.example.lamlaisecurity.entity.Category;
import com.example.lamlaisecurity.repository.CategoryRepository;
import com.example.lamlaisecurity.service.design.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAllByIsDeleteFalse();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAllByIsDelete(pageable, false);
    }

    @Override
    public Page<Category> findAllByName(Pageable pageable, String search) {
        return categoryRepository
                .findAllByIsDeleteAndCategoryNameContainingIgnoreCase(pageable, false, search);
    }

    @Override
    public Category findById(Long cateId) {
        Category category = categoryRepository.findByCategoryIdAndIsDelete(cateId, false).orElse(null);

        if(category == null) {
            throw new AppException("Không tìm thấy danh mục có mã phù hợp", HttpStatus.NOT_FOUND.value());
        }

        return category;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public CategoryDetailResponse findDetail(Long cateId) {
        Category category = findById(cateId);
        return CategoryDetailResponse.builder()
                .category(category)
                .children(category.getChildren())
                .build();
    }

    @Override
    public Integer delete(Long cateId) {
        return categoryRepository.updateIsDeleteByCategoryId(cateId);
    }
}
