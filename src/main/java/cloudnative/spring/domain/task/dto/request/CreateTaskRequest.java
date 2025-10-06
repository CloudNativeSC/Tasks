package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.Priority;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    private String title;
    private String description;
    private Priority priority;
    private Integer estimatedPomodoros;
    private LocalDateTime dueAt;
    private Boolean isRecurring;
    private String categoryId;
}
