package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.StartWorkSessionRequest;
import cloudnative.spring.domain.task.dto.response.WorkSessionResponse;
import cloudnative.spring.domain.task.dto.response.WorkSessionStatsResponse;
import cloudnative.spring.domain.task.entity.WorkSession;
import cloudnative.spring.domain.task.enums.SessionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WorkSessionService {
    // Public API
    WorkSessionResponse startWorkSession(String userId, StartWorkSessionRequest request);
    WorkSessionResponse endWorkSession(String id);
    WorkSessionResponse pauseWorkSession(String id);
    WorkSessionResponse resumeWorkSession(String id);
    List<WorkSessionResponse> getWorkSessionsByUserId(String userId);
    List<WorkSessionResponse> getWorkSessionsByDate(String userId, LocalDate date);
    WorkSessionResponse getWorkSessionById(String id);
    WorkSessionStatsResponse getWorkSessionStats(String userId, LocalDate startDate, LocalDate endDate);

    // Internal API (다른 서비스에서 사용 필요)
    WorkSession start(WorkSession s);
    WorkSession pause(Long id);
    WorkSession complete(Long id);
    List<WorkSession> listByRange(String userId, LocalDateTime from, LocalDateTime to);
    List<WorkSession> listByStatus(String userId, SessionStatus status);
    List<WorkSession> listByTask(String taskId);
}