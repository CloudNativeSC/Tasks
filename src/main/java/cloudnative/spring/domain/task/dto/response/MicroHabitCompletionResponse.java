package cloudnative.spring.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MicroHabitCompletionResponse {
    private MicroHabitResponse habit;
    private String encouragementMessage;
    private Integer completionCount;
}