package cloudnative.spring.domain.checklist.dto.response;


import cloudnative.spring.domain.checklist.entity.ChecklistTemplate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ChecklistTemplateResponse {
    private String id;
    private String name;
    private String description;
    private Boolean isSystemTemplate;
    private Integer itemCount;
    private List<TemplateItemResponse> items;

    @Getter
    @Builder
    public static class TemplateItemResponse {
        private String id;
        private String content;
        private Integer displayOrder;
    }

    /**
     * 엔티티 → DTO (항목 포함)
     */
    public static ChecklistTemplateResponse fromWithItems(ChecklistTemplate template) {
        return ChecklistTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .isSystemTemplate(template.getIsSystemTemplate())
                .itemCount(template.getItems().size())
                .items(template.getItems().stream()
                        .map(item -> TemplateItemResponse.builder()
                                .id(item.getId())
                                .content(item.getContent())
                                .displayOrder(item.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 엔티티 → DTO (항목 제외)
     */
    public static ChecklistTemplateResponse from(ChecklistTemplate template) {
        return ChecklistTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .isSystemTemplate(template.getIsSystemTemplate())
                .itemCount(template.getItems().size())
                .build();
    }
}