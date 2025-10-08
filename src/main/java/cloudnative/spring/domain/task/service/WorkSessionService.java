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

    // 작업 시작 (새로운 sessionId 생성)
    WorkSessionResponse startTaskSession(String userId, String taskId);

    // 뽀모도로 완료 (다음 세션 자동 생성)
    WorkSessionResponse completePomodoro(String workSessionId);

    // 작업 전체 완료 (sessionId 그룹 전체 완료)
    WorkSessionResponse completeTaskSession(Long sessionId, String userId);

    // sessionId로 세션 그룹 조회
    List<WorkSessionResponse> getSessionsBySessionId(Long sessionId);
}