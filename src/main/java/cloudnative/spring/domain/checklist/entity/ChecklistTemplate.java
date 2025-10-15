package cloudnative.spring.domain.checklist.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklist_templates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChecklistTemplate extends BaseTimeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system_template", nullable = false)
    @Builder.Default
    private Boolean isSystemTemplate = false;

    @Column(name = "created_by")
    private String createdBy;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistTemplateItem> items = new ArrayList<>();

    public void addItem(ChecklistTemplateItem item) {
        items.add(item);
        item.setTemplate(this);
    }
}