package cloudnative.spring.domain.task.dto.response;


import cloudnative.spring.domain.task.entity.WorkSession;
import cloudnative.spring.domain.task.enums.SessionStatus;
import cloudnative.spring.domain.task.enums.SessionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkSessionResponse {
    private Long id;
    private Long sessionId;
    private Long blockId;
    private SessionType sessionType;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private String notes;
    private String interruptions;
    private Integer focusScore;
    private String userId;
    private String taskId;
    private String taskTitle;
    private Integer actualDurationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WorkSessionResponse from(WorkSession session) {
        return WorkSessionResponse.builder()
                .id(session.getId())
                .sessionId(session.getSessionId())
                .blockId(session.getBlockId())
                .sessionType(session.getSessionType())
                .durationMinutes(session.getDurationMinutes())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .notes(session.getNotes())
                .interruptions(session.getInterruptions())
                .focusScore(session.getFocusScore())
                .userId(session.getUserId())
                .taskId(session.getTaskId())
                .taskTitle(session.getTask() != null ? session.getTask().getTitle() : null)
                .actualDurationMinutes(session.getActualDurationMinutes())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}