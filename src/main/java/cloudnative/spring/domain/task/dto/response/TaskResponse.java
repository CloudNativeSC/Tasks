package cloudnative.spring.domain.task.dto.response;

import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.Priority;
import cloudnative.spring.domain.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

import cloudnative.spring.domain.task.dto.response.CategoryResponse;

@Getter
@Builder
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private Integer estimatedPomodoros;
    private Integer completedPomodoros;
    private LocalDateTime dueAt;
    private CategoryResponse category;

    // Entity → DTO 변환
    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .estimatedPomodoros(task.getEstimatedPomodoros())
                .completedPomodoros(task.getCompletedPomodoros())
                .dueAt(task.getDueAt())
                .category(CategoryResponse.from(task.getCategory()))
                .build();
    }
}

