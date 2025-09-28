package cloudnative.spring.domain.task.entity;


import cloudnative.spring.domain.task.enums.TemplateType;
import jakarta.persistence.*;
import lombok.*;
import cloudnative.spring.global.entity.BaseTimeEntity;

@Entity
@Table(name = "task_templates")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TaskTemplate extends BaseTimeEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_pomodoros")
    private Integer estimatedPomodoros;

    @Column(columnDefinition = "JSON")
    private String tags;

    @Column(name = "is_ai_generated")
    @Builder.Default
    private Boolean isAiGenerated = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    @Builder.Default
    private TemplateType templateType = TemplateType.CUSTOM;

    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    // N:1 관계 - N개 TaskTemplate은 하나의 Category에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    public void incrementUsage() {
        this.usageCount = (this.usageCount == null ? 0 : this.usageCount) + 1;
    }

    public Task createTaskFromTemplate(String userId, String taskId) {
        return Task.builder()
                .id(taskId)
                .userId(userId)
                .title(this.title)
                .description(this.description)
                .estimatedPomodoros(this.estimatedPomodoros)
                .category(this.category)  //Category 객체 직접 설정
                .build();
    }
}