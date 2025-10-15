package cloudnative.spring.domain.checklist.dto.response;

import cloudnative.spring.domain.checklist.entity.UserChecklist;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserChecklistResponse {
    private String id;
    private String userId;
    private String name;
    private String templateId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Integer totalItems;
    private Integer checkedItems;
    private Double progress;
    private List<ChecklistItemResponse> items;

    @Getter
    @Builder
    public static class ChecklistItemResponse {
        private String id;
        private String content;
        private Boolean isChecked;
        private Integer displayOrder;
        private LocalDateTime checkedAt;
    }

    /**
     * 엔티티 → DTO (항목 포함)
     */
    public static UserChecklistResponse fromWithItems(UserChecklist checklist) {
        long checkedCount = checklist.getItems().stream()
                .filter(item -> item.getIsChecked())
                .count();

        return UserChecklistResponse.builder()
                .id(checklist.getId())
                .userId(checklist.getUserId())
                .name(checklist.getName())
                .templateId(checklist.getTemplateId())
                .createdAt(checklist.getCreatedAt())
                .completedAt(checklist.getCompletedAt())
                .totalItems(checklist.getItems().size())
                .checkedItems((int) checkedCount)
                .progress(Math.round(checklist.getProgress() * 100.0) / 100.0)
                .items(checklist.getItems().stream()
                        .map(item -> ChecklistItemResponse.builder()
                                .id(item.getId())
                                .content(item.getContent())
                                .isChecked(item.getIsChecked())
                                .displayOrder(item.getDisplayOrder())
                                .checkedAt(item.getCheckedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 엔티티 → DTO (항목 제외)
     */
    public static UserChecklistResponse from(UserChecklist checklist) {
        long checkedCount = checklist.getItems().stream()
                .filter(item -> item.getIsChecked())
                .count();

        return UserChecklistResponse.builder()
                .id(checklist.getId())
                .userId(checklist.getUserId())
                .name(checklist.getName())
                .templateId(checklist.getTemplateId())
                .createdAt(checklist.getCreatedAt())
                .completedAt(checklist.getCompletedAt())
                .totalItems(checklist.getItems().size())
                .checkedItems((int) checkedCount)
                .progress(Math.round(checklist.getProgress() * 100.0) / 100.0)
                .build();
    }
}