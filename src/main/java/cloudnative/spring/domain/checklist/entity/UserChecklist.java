package cloudnative.spring.domain.checklist.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_checklists")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserChecklist extends BaseTimeEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> items = new ArrayList<>();

    public boolean isAllCompleted() {
        if (items.isEmpty()) {
            return false;
        }
        return items.stream().allMatch(ChecklistItem::getIsChecked);
    }

    public void markAsCompleted() {
        if (isAllCompleted() && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public void unmarkCompleted() {
        if (!isAllCompleted() && this.completedAt != null) {
            this.completedAt = null;
        }
    }

    public double getProgress() {
        if (items.isEmpty()) {
            return 0.0;
        }
        long checkedCount = items.stream().filter(ChecklistItem::getIsChecked).count();
        return (checkedCount * 100.0) / items.size();
    }

    public void addItem(ChecklistItem item) {
        items.add(item);
        item.setChecklist(this);
    }
}
