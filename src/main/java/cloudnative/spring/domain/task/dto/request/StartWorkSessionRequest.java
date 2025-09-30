package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.SessionType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartWorkSessionRequest {
    private String taskId;
    private SessionType sessionType;
    private Integer durationMinutes;
    private String notes;
}