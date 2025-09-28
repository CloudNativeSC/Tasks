package cloudnative.spring.domain.task.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateTaskRequest {

    private String title;
    private String description;
    private Priority priority;
    private Integer estimatedPomodoros;
    private LocalDateTime dueAt;
    private Boolean isRecurring;
    private String categoryId;

}
