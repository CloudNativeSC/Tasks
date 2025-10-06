package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.SessionStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkSessionRequest {
    
    private SessionStatus status;
    
    @Size(max = 1000, message = "메모는 1000자를 초과할 수 없습니다.")
    private String notes;
    
    private String interruptions;
    
    @Min(value = 0, message = "집중도 점수는 0 이상이어야 합니다.")
    @Max(value = 100, message = "집중도 점수는 100 이하여야 합니다.")
    private Integer focusScore;
    
    private LocalDateTime endTime;
}
