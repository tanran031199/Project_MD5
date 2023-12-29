package com.example.lamlaisecurity.api.admin;

import com.example.lamlaisecurity.dto.response.ProductResponse;
import com.example.lamlaisecurity.entity.Product;
import com.example.lamlaisecurity.entity.ProductImage;
import com.example.lamlaisecurity.service.design.ProductImageService;
import com.example.lamlaisecurity.service.design.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/product")
public class AdminProductApi {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;

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

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getDetail(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.findById(productId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestParam(name = "images") List<MultipartFile> images,
            @ModelAttribute Product product
    ) {
        Product newProduct = productService.save(product);
        List<ProductImage> productImages = productImageService.save(newProduct.getProductId(), images);
        ProductResponse productResponse = ProductResponse.builder()
                .product(newProduct)
                .productImages(productImages)
                .build();

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long productId,
            @RequestParam(name = "images", required = false) List<MultipartFile> images,
            @ModelAttribute Product product
    ) {
        product.setProductId(productId);
        product = productService.save(product);
        List<ProductImage> productImages;

        if (images == null || images.isEmpty()) {
            productImages = productImageService.findAllByProductId(productId);
        } else {
            productImageService.deleteAllByProductId(productId);
            productImages = productImageService.save(productId, images);
        }

        ProductResponse productResponse = ProductResponse.builder()
                .product(product)
                .productImages(productImages)
                .build();

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @DeleteMapping("/productImage/{productImgId}")
    public ResponseEntity<String> deleteProductImage(@PathVariable Long productImgId) {
        productImageService.deleteById(productImgId);
        return new ResponseEntity<>("Xóa ảnh sản phẩm thành công!", HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> delete(@PathVariable Long productId) {
        productService.deleteById(productId);
        return new ResponseEntity<>("Xóa sản phẩm thành công!", HttpStatus.OK);
    }
}
