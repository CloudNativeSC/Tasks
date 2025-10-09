package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    // 카테고리 목록 조회 (정렬 순서대로)
    List<CategoryResponse> getAllCategories();

    // 카테고리 상세 조회
    CategoryResponse getCategoryById(String categoryId);
}