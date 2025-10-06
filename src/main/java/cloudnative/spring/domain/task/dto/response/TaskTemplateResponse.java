package cloudnative.spring.domain.task.dto.response;


import cloudnative.spring.domain.task.entity.TaskTemplate;
import cloudnative.spring.domain.task.enums.TemplateType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskTemplateResponse {
    private String id;
    private String templateName;
    private String title;
    private String description;
    private Integer estimatedPomodoros;
    private String tags;
    private Boolean isAiGenerated;
    private TemplateType templateType;
    private Integer usageCount;
    private String categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskTemplateResponse from(TaskTemplate template) {
        return TaskTemplateResponse.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .title(template.getTitle())
                .description(template.getDescription())
                .estimatedPomodoros(template.getEstimatedPomodoros())
                .tags(template.getTags())
                .isAiGenerated(template.getIsAiGenerated())
                .templateType(template.getTemplateType())
                .usageCount(template.getUsageCount())
                .categoryId(template.getCategoryId())
                .categoryName(template.getCategory() != null ? template.getCategory().getName() : null)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}