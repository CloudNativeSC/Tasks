package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.SessionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateWorkSessionRequest {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "작업 ID는 필수입니다.")
    private String taskId;

    @NotNull(message = "세션 타입은 필수입니다.")
    private SessionType sessionType;

    @NotNull(message = "세션 시간은 필수입니다.")
    @Min(value = 1, message = "세션 시간은 최소 1분 이상이어야 합니다.")
    @Max(value = 120, message = "세션 시간은 최대 120분까지 가능합니다.")
    private Integer durationMinutes;

    private LocalDateTime startTime;
}
