package com.example.lamlaisecurity.service.design;

import com.example.lamlaisecurity.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImage> save(Long productId, List<MultipartFile> images);

    void deleteAllByProductId(Long productId);

    List<ProductImage> findAllByProductId(Long productId);

    void deleteById(Long productImageId);
}
