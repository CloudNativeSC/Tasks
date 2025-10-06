package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.SessionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartWorkSessionRequest {
    
    @NotBlank(message = "작업 ID는 필수입니다.")
    private String taskId;
    
    @NotNull(message = "세션 타입은 필수입니다.")
    private SessionType sessionType;
    
    @NotNull(message = "세션 시간은 필수입니다.")
    @Min(value = 1, message = "세션 시간은 최소 1분 이상이어야 합니다.")
    @Max(value = 120, message = "세션 시간은 최대 120분까지 가능합니다.")
    private Integer durationMinutes;
    
    @Size(max = 1000, message = "메모는 1000자를 초과할 수 없습니다.")
    private String notes;
}