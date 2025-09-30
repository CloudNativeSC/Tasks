package cloudnative.spring.domain.task.dto.request;

import cloudnative.spring.domain.task.enums.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CreateWorkSessionRequest {
    private String userId;           // 필수
    private String taskId;           // 필수
    private SessionType sessionType; // 필수 (WORK / SHORT_BREAK / LONG_BREAK)
    private Integer durationMinutes; // 필수
    private LocalDateTime startTime; // 선택 (없으면 now)
}
