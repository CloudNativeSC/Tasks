package cloudnative.spring.domain.task.entity;


import cloudnative.spring.domain.task.enums.Priority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import cloudnative.spring.global.entity.BaseTimeEntity;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.enums.RecurringPattern;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_tasks_user_id", columnList = "user_id"),
        @Index(name = "idx_tasks_status", columnList = "status"),
        @Index(name = "idx_tasks_due_at", columnList = "due_at")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task extends BaseTimeEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "estimated_pomodoros")
    private Integer estimatedPomodoros;

    @Column(name = "completed_pomodoros")
    @Builder.Default
    private Integer completedPomodoros = 0;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern")
    private RecurringPattern recurringPattern;

    // 스케줄링
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_start_time")
    private LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalTime scheduledEndTime;


    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // N:1 관계 - 여러 Task는 하나의 Category에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 비즈니스 메소드
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void addCompletedPomodoro() {
        this.completedPomodoros = (this.completedPomodoros == null ? 0 : this.completedPomodoros) + 1;
    }

    public void updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        if (newStatus == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}