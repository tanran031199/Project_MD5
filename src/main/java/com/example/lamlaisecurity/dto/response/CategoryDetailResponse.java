package com.example.lamlaisecurity.dto.response;

import com.example.lamlaisecurity.entity.Category;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDetailResponse {
    private Category category;
    private List<Category> children;
}
