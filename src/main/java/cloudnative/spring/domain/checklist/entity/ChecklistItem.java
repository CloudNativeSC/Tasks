package cloudnative.spring.domain.checklist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "checklist_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChecklistItem {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private UserChecklist checklist;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_checked", nullable = false)
    @Builder.Default
    private Boolean isChecked = false;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    public void toggle() {
        this.isChecked = !this.isChecked;
        this.checkedAt = this.isChecked ? LocalDateTime.now() : null;
    }
}