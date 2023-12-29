package com.example.lamlaisecurity.api.all;

import com.example.lamlaisecurity.dto.response.ProductResponse;
import com.example.lamlaisecurity.service.design.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
public class ProductApi {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(name = "cate", required = false) Long cateId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "productId") String sortBy,
            @RequestParam(name = "orderBy", required = false, defaultValue = "asc") String orderBy
    ) {
        Pageable pageable;
        Page<ProductResponse> productPage;

        if (orderBy.equals("asc")) {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        }

        if (search == null) {
            if (cateId == null) {
                productPage = productService.findAll(pageable);
            } else {
                productPage = productService.findAllByCategory(pageable, cateId);
            }
        } else {
            if (cateId == null) {
                productPage = productService.findAllByName(pageable, search);
            } else {
                productPage = productService.findAllByNameAndCategory(pageable, cateId, search);
            }
        }


        Map<String, Object> data = new HashMap<>();
        data.put("products", productPage.getContent());
        data.put("pageSize", productPage.getSize());
        data.put("currentPage", productPage.getNumber());
        data.put("totalElement", productPage.getTotalElements());
        data.put("totalPage", productPage.getTotalPages());

        if (productPage.isEmpty()) {
            return new ResponseEntity<>("Chưa có sản phẩm nào", HttpStatus.OK);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/new-product")
    public ResponseEntity<?> getAllNewProduct() {
        List<ProductResponse> productResponses = productService.findAllNewProduct();
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }
}
