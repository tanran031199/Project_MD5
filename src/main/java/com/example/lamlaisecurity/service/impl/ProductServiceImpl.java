package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.dto.response.ProductResponse;
import com.example.lamlaisecurity.entity.Product;
import com.example.lamlaisecurity.repository.CartItemRepository;
import com.example.lamlaisecurity.repository.ProductRepository;
import com.example.lamlaisecurity.service.design.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByIsDelete(pageable, false);

        return productPage.map(item -> ProductResponse.builder()
                .product(item)
                .productImages(item.getProductImages())
                .build());
    }

    @Override
    public List<ProductResponse> findAllNewProduct() {
        List<Product> products = productRepository.findAllByIsDeleteFalseOrderByTimeStampDesc();

        return products.stream().map(item -> ProductResponse.builder()
                .product(item)
                .productImages(item.getProductImages())
                .build()).toList();
    }

    @Override
    public Page<ProductResponse> findAllByCategory(Pageable pageable, Long cateId) {
        Page<Product> productPage = productRepository.findAllByIsDelete(pageable, cateId, false);

        return productPage.map(item -> ProductResponse.builder()
                .product(item)
                .productImages(item.getProductImages())
                .build());
    }

    @Override
    public Page<ProductResponse> findAllByName(Pageable pageable, String search) {
        Page<Product> productPage = productRepository
                .findAllByIsDeleteAndProductNameContainingIgnoreCase(pageable, false, search);

        return productPage.map(item -> ProductResponse.builder()
                .product(item)
                .productImages(item.getProductImages())
                .build());
    }

    @Override
    public Page<ProductResponse> findAllByNameAndCategory(Pageable pageable, Long cateId, String search) {
        Page<Product> productPage = productRepository
                .findAllByIsDeleteAndProductNameContainingIgnoreCase(pageable, cateId, search, false);

        return productPage.map(item -> ProductResponse.builder()
                .product(item)
                .productImages(item.getProductImages())
                .build());
    }

    @Override
    public ProductResponse findById(Long productId) {
        Product product = productRepository.findByProductIdAndIsDelete(productId, false).orElse(null);

        if (product == null) {
            throw new AppException("Không tìm thấy sản phẩm có mã phù hợp", HttpStatus.NOT_FOUND.value());
        }

        return ProductResponse.builder()
                .product(product)
                .productImages(product.getProductImages())
                .build();
    }

    @Override
    public Product save(Product product) {
        if (product.getProductId() != null) {
            if (!productRepository.existsById(product.getProductId())) {
                throw new AppException("Không tìm thấy sản phẩm có mã phù hợp", HttpStatus.NOT_FOUND.value());
            }
        }

        double importPrice = product.getImportPrice();
        double interestPercent = product.getInterestPercent();
        product.setExportPrice((importPrice * interestPercent / 100) + importPrice);
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Không tìm thấy sản phẩm có mã phù hợp", HttpStatus.NOT_FOUND.value()));
        product.setIsDelete(true);
        productRepository.save(product);
    }
}
