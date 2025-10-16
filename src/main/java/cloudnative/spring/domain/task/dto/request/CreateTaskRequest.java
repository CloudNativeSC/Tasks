package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.Priority;
import cloudnative.spring.domain.task.enums.RecurringPattern;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    
    @NotBlank(message = "작업 제목은 필수입니다.")
    @Size(min = 1, max = 200, message = "작업 제목은 1자 이상 200자 이하여야 합니다.")
    private String title;
    
    @Size(max = 2000, message = "작업 설명은 2000자를 초과할 수 없습니다.")
    private String description;
    
    @NotNull(message = "우선순위는 필수입니다.")
    private Priority priority;
    
    @Min(value = 1, message = "예상 뽀모도로 개수는 최소 1개 이상이어야 합니다.")
    @Max(value = 20, message = "예상 뽀모도로 개수는 최대 20개까지 가능합니다.")
    private Integer estimatedPomodoros;
    
    private LocalDateTime dueAt;
    
    private Boolean isRecurring;
    
    @NotBlank(message = "카테고리 ID는 필수입니다.")
    private String categoryId;

    /**
     * 반복 패턴 (DAILY, WEEKLY, MONTHLY)
     * isRecurring이 true일 때 필수
     */

    // 1. 반복 패턴
    private RecurringPattern recurringPattern;

    // 2. 스케줄 날짜
    private LocalDate scheduledDate;

    // 3. 시작 시간
    private LocalTime scheduledStartTime;

    // 4. 종료 시간
    private LocalTime scheduledEndTime;
}
