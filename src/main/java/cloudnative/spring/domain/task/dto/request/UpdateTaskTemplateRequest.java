package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.TemplateType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskTemplateRequest {
    
    @Size(min = 1, max = 100, message = "템플릿 이름은 1자 이상 100자 이하여야 합니다.")
    private String templateName;
    
    @Size(min = 1, max = 200, message = "작업 제목은 1자 이상 200자 이하여야 합니다.")
    private String title;
    
    @Size(max = 2000, message = "작업 설명은 2000자를 초과할 수 없습니다.")
    private String description;
    
    @Min(value = 1, message = "예상 뽀모도로 개수는 최소 1개 이상이어야 합니다.")
    @Max(value = 20, message = "예상 뽀모도로 개수는 최대 20개까지 가능합니다.")
    private Integer estimatedPomodoros;
    
    @Size(max = 500, message = "태그는 500자를 초과할 수 없습니다.")
    private String tags;
    
    private TemplateType templateType;
    
    private String categoryId;
}
