package cloudnative.spring.domain.task.dto.response;

import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.Priority;
import cloudnative.spring.domain.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class TaskResponse {
    private String id;
    private String userId;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private Integer estimatedPomodoros;
    private Integer completedPomodoros;
    private LocalDateTime dueAt;
    private Boolean isRecurring;
    private LocalDate scheduledDate;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private LocalDateTime completedAt;
    private String categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String googleCalendarEventId;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .estimatedPomodoros(task.getEstimatedPomodoros())
                .completedPomodoros(task.getCompletedPomodoros())
                .dueAt(task.getDueAt())
                .isRecurring(task.getIsRecurring())
                .scheduledDate(task.getScheduledDate())
                .scheduledStartTime(task.getScheduledStartTime())
                .scheduledEndTime(task.getScheduledEndTime())
                .completedAt(task.getCompletedAt())
                .categoryId(task.getCategory() != null ? task.getCategory().getId() : null)
                .categoryName(task.getCategory() != null ? task.getCategory().getName() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}