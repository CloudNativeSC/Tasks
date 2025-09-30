package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.TemplateType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskTemplateRequest {
    private String templateName;
    private String title;
    private String description;
    private Integer estimatedPomodoros;
    private String tags;
    private Boolean isAiGenerated;
    private TemplateType templateType;
    private String categoryId;
}