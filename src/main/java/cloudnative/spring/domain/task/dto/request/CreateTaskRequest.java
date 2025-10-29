package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.Priority;
import cloudnative.spring.domain.task.enums.RecurringPattern;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "할 일 생성 요청")
public class CreateTaskRequest {

    @Schema(description = "작업 제목", example = "프로젝트 기획서 작성", required = true)
    @NotBlank(message = "작업 제목은 필수입니다.")
    @Size(min = 1, max = 200, message = "작업 제목은 1자 이상 200자 이하여야 합니다.")
    private String title;

    @Schema(description = "작업 설명", example = "내일까지 완성해야 함")
    @Size(max = 2000, message = "작업 설명은 2000자를 초과할 수 없습니다.")
    private String description;

    @Schema(description = "우선순위", example = "MEDIUM", required = true, allowableValues = {"HIGH", "MEDIUM", "LOW"})
    @NotNull(message = "우선순위는 필수입니다.")
    private Priority priority;

    @Schema(description = "예상 뽀모도로 개수", example = "2", required = true, minimum = "1", maximum = "20")
    @Min(value = 1, message = "예상 뽀모도로 개수는 최소 1개 이상이어야 합니다.")
    @Max(value = 20, message = "예상 뽀모도로 개수는 최대 20개까지 가능합니다.")
    private Integer estimatedPomodoros;

    @Schema(description = "마감일", example = "2025-10-28T18:00:00")
    private LocalDateTime dueAt;

    @Schema(description = "반복 여부", example = "false")
    private Boolean isRecurring;

    @Schema(description = "카테고리 ID", example = "cat-001", required = true)
    @NotBlank(message = "카테고리 ID는 필수입니다.")
    private String categoryId;

    @Schema(description = "반복 패턴 (isRecurring이 true일 때 필수)", example = "DAILY", allowableValues = {"DAILY", "WEEKLY", "MONTHLY"})
    private RecurringPattern recurringPattern;

    @Schema(description = "스케줄 날짜", example = "2025-10-27")
    private LocalDate scheduledDate;

    @Schema(description = "시작 시간", example = "14:00:00")
    private LocalTime scheduledStartTime;

    @Schema(description = "종료 시간", example = "15:00:00")
    private LocalTime scheduledEndTime;
}