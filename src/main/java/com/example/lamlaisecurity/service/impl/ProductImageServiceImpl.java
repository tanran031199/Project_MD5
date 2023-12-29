package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.entity.Product;
import com.example.lamlaisecurity.entity.ProductImage;
import com.example.lamlaisecurity.repository.ProductImageRepository;
import com.example.lamlaisecurity.service.design.ProductImageService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;
    @Value("${upload.product-path}")
    private String uploadPath;

    @Override
    public List<ProductImage> save(Long productId, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            String fileName = file.getOriginalFilename();

            try {
                File uploadFolder = new File(uploadPath);

                if(!uploadFolder.exists()) {
                    if(uploadFolder.mkdir()) {
                        log.info("Tạo File Upload thành công");
                    } else {
                        log.error("Tạo File Upload thất bại");
                    }
                }

                FileCopyUtils.copy(file.getBytes(), new File(uploadPath + "\\" + fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ProductImage productImage = ProductImage.builder()
                    .product(Product.builder().productId(productId).build())
                    .fileName(uploadPath + "\\" + fileName)
                    .build();

            productImageRepository.save(productImage);
        }

        return findAllByProductId(productId);
    }

    @Override
    public void deleteAllByProductId(Long productId) {
        productImageRepository.deleteAllByProductId(productId);
    }

    @Override
    public List<ProductImage> findAllByProductId(Long productId) {
        return productImageRepository.findAllByProductId(productId);
    }

    @Override
    public void deleteById(Long productImageId) {
        if(!productImageRepository.existsById(productImageId)) {
            throw new AppException("Không tìm thấy ảnh sản phẩm có mã phù hợp", HttpStatus.NOT_FOUND.value());
        }

        productImageRepository.deleteById(productImageId);
    }
}
