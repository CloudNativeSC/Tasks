package cloudnative.spring.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskStatsEnhancedResponse {
    // 기본 통계
    private Long totalTasks;
    private Long completedTasks;
    private Long todoTasks;
    private Double completionRate;

    // 이번 주 통계
    private Long weekTotalTasks;
    private Long weekCompletedTasks;
    private Double weekCompletionRate;

    // 뽀모도로 통계
    private Long totalPomodoros;
    private Long todayPomodoros;
    private Long weekPomodoros;
    private Long monthPomodoros;

    // 집중 시간 (시간 단위)
    private Double totalFocusHours;
    private Double todayFocusHours;
    private Double weekFocusHours;
    private Double monthFocusHours;
}