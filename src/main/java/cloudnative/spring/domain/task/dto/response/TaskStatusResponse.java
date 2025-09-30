package cloudnative.spring.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskStatusResponse {
    private Long totalTasks;
    private Long completedTasks;
    private Long todoTasks;
    private Double completionRate;
}