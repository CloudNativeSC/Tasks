package cloudnative.spring.domain.task.dto.response;


import cloudnative.spring.domain.task.entity.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskTagResponse {
    private Long id;
    private String name;
    private String categoryId;
    private String colorCode;
    private Boolean isDefault;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskTagResponse from(Tag tag) {
        return TaskTagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .categoryId(tag.getCategoryId())
                .colorCode(tag.getColorCode())
                .isDefault(tag.getIsDefault())
                .usageCount(tag.getUsageCount())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}