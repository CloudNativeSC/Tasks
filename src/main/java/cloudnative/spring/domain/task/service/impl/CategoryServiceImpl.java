package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.dto.response.CategoryResponse;
import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.service.CategoryService;
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        log.debug("카테고리 목록 조회");
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        log.debug("조회된 카테고리 수: {}", categories.size());
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(String categoryId) {
        log.debug("카테고리 상세 조회 - categoryId: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("카테고리를 찾을 수 없음 - categoryId: {}", categoryId);
                    return new GeneralHandler(ErrorCode.CATEGORY_NOT_FOUND);
                });
        return CategoryResponse.from(category);
    }
}