package cloudnative.spring.domain.task.dto.response;

import cloudnative.spring.domain.task.entity.TaskReflection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskReflectionResponse {
    private Long id;
    private String userId;
    private String taskId;
    private Long workSessionId;
    private String warmupNote;
    private String cooldownNote;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskReflectionResponse from(TaskReflection reflection) {
        return TaskReflectionResponse.builder()
                .id(reflection.getId())
                .userId(reflection.getUserId())
                .taskId(reflection.getTaskId())
                .workSessionId(reflection.getWorkSessionId())
                .warmupNote(reflection.getWarmupNote())
                .cooldownNote(reflection.getCooldownNote())
                .categoryName(reflection.getCategoryName())
                .createdAt(reflection.getCreatedAt())
                .updatedAt(reflection.getUpdatedAt())
                .build();
    }
}