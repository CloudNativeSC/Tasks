package cloudnative.spring.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskStatusResponse {
    private long totalTasks;
    private long completedTasks;
    private long todoTasks;
    private double completionRate;
}