package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.Priority;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.enums.RecurringPattern;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {
    
    @Size(min = 1, max = 200, message = "작업 제목은 1자 이상 200자 이하여야 합니다.")
    private String title;
    
    @Size(max = 2000, message = "작업 설명은 2000자를 초과할 수 없습니다.")
    private String description;
    
    private Priority priority;
    
    private TaskStatus status;
    
    @Min(value = 1, message = "예상 뽀모도로 개수는 최소 1개 이상이어야 합니다.")
    @Max(value = 20, message = "예상 뽀모도로 개수는 최대 20개까지 가능합니다.")
    private Integer estimatedPomodoros;
    
    private LocalDateTime dueAt;
    
    private Boolean isRecurring;
    
    private RecurringPattern recurringPattern;
    
    private LocalDate scheduledDate;
    
    private LocalTime scheduledStartTime;
    
    private LocalTime scheduledEndTime;
    
    private String categoryId;
}
