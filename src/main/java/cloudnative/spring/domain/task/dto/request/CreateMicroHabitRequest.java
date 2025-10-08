package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.HabitType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMicroHabitRequest {

    @NotBlank(message = "습관 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "습관 이름은 1자 이상 100자 이하여야 합니다.")
    private String habitName;

    @NotNull(message = "소요 시간은 필수입니다.")
    @Min(value = 1, message = "소요 시간은 최소 1분 이상이어야 합니다.")
    @Max(value = 30, message = "소요 시간은 최대 30분까지 가능합니다.")
    private Integer durationMinutes;

    @NotNull(message = "습관 타입은 필수입니다.")
    private HabitType habitType;
}