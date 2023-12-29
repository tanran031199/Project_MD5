package com.example.lamlaisecurity.api.admin;

import com.example.lamlaisecurity.dto.response.CategoryDetailResponse;
import com.example.lamlaisecurity.entity.Category;
import com.example.lamlaisecurity.service.design.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/category")
public class AdminCategoryApi {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "categoryId") String sortBy,
            @RequestParam(name = "orderBy", required = false, defaultValue = "asc") String orderBy
    ) {
        Pageable pageable;
        Page<Category> categoriePage;

        if (orderBy.equals("asc")) {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        }

        if (search == null) {
            categoriePage = categoryService.findAll(pageable);
        } else {
            categoriePage = categoryService.findAllByName(pageable, search);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoriePage.getContent());
        data.put("pageSize", categoriePage.getSize());
        data.put("currentPage", categoriePage.getNumber());
        data.put("totalElement", categoriePage.getTotalElements());
        data.put("totalPage", categoriePage.getTotalPages());

        if (categoriePage.isEmpty()) {
            return new ResponseEntity<>("Chưa có danh mục nào", HttpStatus.OK);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/{cateId}")
    public ResponseEntity<CategoryDetailResponse> getDetail(@PathVariable(name = "cateId") Long cateId) {
        return new ResponseEntity<>(categoryService.findDetail(cateId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.save(category), HttpStatus.OK);
    }

    @PutMapping("/{cateId}")
    public ResponseEntity<Category> update(@PathVariable(name = "cateId") Long cateId,
                                           @RequestBody Category category) {
        category.setCategoryId(cateId);
        return new ResponseEntity<>(categoryService.save(category), HttpStatus.OK);
    }

    @DeleteMapping("/{cateId}")
    public ResponseEntity<String> delete(@PathVariable(name = "cateId") Long cateId) {
        int isDelete = categoryService.delete(cateId);

        if (isDelete > 0) {
            return new ResponseEntity<>("Xóa danh mục thành công", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Xóa danh mục thất bại", HttpStatus.BAD_REQUEST);
        }
    }
}
