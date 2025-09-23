package cloudnative.spring.domain.task.dto.response;

import cloudnative.spring.domain.task.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String colorCode;
    private String iconImage;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .colorCode(category.getColorCode())
                .iconImage(category.getIconImage())
                .build();
    }
}