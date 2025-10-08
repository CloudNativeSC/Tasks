package cloudnative.spring.domain.task.dto.response;

import cloudnative.spring.domain.task.entity.MicroHabit;
import cloudnative.spring.domain.task.enums.HabitType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MicroHabitResponse {
    private Long id;
    private String userId;
    private String habitName;
    private Integer durationMinutes;
    private Integer completionCount;
    private LocalDateTime lastCompletedAt;
    private HabitType habitType;
    private LocalDateTime createdAt;

    public static MicroHabitResponse from(MicroHabit habit) {
        return MicroHabitResponse.builder()
                .id(habit.getId())
                .userId(habit.getUserId())
                .habitName(habit.getHabitName())
                .durationMinutes(habit.getDurationMinutes())
                .completionCount(habit.getCompletionCount())
                .lastCompletedAt(habit.getLastCompletedAt())
                .habitType(habit.getHabitType())
                .createdAt(habit.getCreatedAt())
                .build();
    }
}