package cloudnative.spring.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkSessionStatsResponse {
    private Long totalSessions;
    private Long completedSessions;
    private Integer totalMinutes;
    private Double averageFocusScore;
    private Integer totalWorkSessions;
    private Integer totalBreakSessions;
}