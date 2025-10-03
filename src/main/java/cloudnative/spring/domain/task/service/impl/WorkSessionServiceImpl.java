package cloudnative.spring.domain.task.service.impl;


import cloudnative.spring.domain.task.dto.request.StartWorkSessionRequest;
import cloudnative.spring.domain.task.dto.response.WorkSessionResponse;
import cloudnative.spring.domain.task.dto.response.WorkSessionStatsResponse;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.entity.WorkSession;
import cloudnative.spring.domain.task.enums.SessionStatus;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.repository.WorkSessionRepository;
import cloudnative.spring.domain.task.service.WorkSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSessionServiceImpl implements WorkSessionService {

    private final WorkSessionRepository workSessionRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public WorkSessionResponse startWorkSession(String userId, StartWorkSessionRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없습니다: " + request.getTaskId()));

        WorkSession session = WorkSession.builder()
                .userId(userId)
                .taskId(request.getTaskId())
                .sessionType(request.getSessionType())
                .durationMinutes(request.getDurationMinutes())
                .startTime(LocalDateTime.now())
                .status(SessionStatus.ACTIVE)
                .notes(request.getNotes())
                .build();

        WorkSession savedSession = workSessionRepository.save(session);
        return WorkSessionResponse.from(savedSession);
    }

    @Override
    @Transactional
    public WorkSessionResponse endWorkSession(String id) {
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));

        session.completeSession();
        return WorkSessionResponse.from(session);
    }

    @Override
    @Transactional
    public WorkSessionResponse pauseWorkSession(String id) {
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));

        session.pauseSession();
        return WorkSessionResponse.from(session);
    }

    @Override
    @Transactional
    public WorkSessionResponse resumeWorkSession(String id) {
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));

        session.setStatus(SessionStatus.ACTIVE);
        return WorkSessionResponse.from(session);
    }

    @Override
    public List<WorkSessionResponse> getWorkSessionsByUserId(String userId) {
        List<WorkSession> sessions = workSessionRepository.findByUserId(userId);
        return sessions.stream()
                .map(WorkSessionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkSessionResponse> getWorkSessionsByDate(String userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<WorkSession> sessions = workSessionRepository.findByUserIdAndStartTimeBetween(
                userId, startOfDay, endOfDay);

        return sessions.stream()
                .map(WorkSessionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public WorkSessionResponse getWorkSessionById(String id) {
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: " + id));

        return WorkSessionResponse.from(session);
    }

    @Override
    public WorkSessionStatsResponse getWorkSessionStats(String userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();

        List<WorkSession> sessions = workSessionRepository.findByUserIdAndStartTimeBetween(userId, start, end);

        long totalSessions = sessions.size();
        long completedSessions = sessions.stream()
                .filter(s -> s.getStatus() == SessionStatus.COMPLETED)
                .count();

        int totalMinutes = sessions.stream()
                .filter(s -> s.getActualDurationMinutes() != null)
                .mapToInt(WorkSession::getActualDurationMinutes)
                .sum();

        double avgFocusScore = sessions.stream()
                .filter(s -> s.getFocusScore() != null)
                .mapToInt(WorkSession::getFocusScore)
                .average()
                .orElse(0.0);

        long workSessions = sessions.stream()
                .filter(WorkSession::isWorkSession)
                .count();

        return WorkSessionStatsResponse.builder()
                .totalSessions(totalSessions)
                .completedSessions(completedSessions)
                .totalMinutes(totalMinutes)
                .averageFocusScore(avgFocusScore)
                .totalWorkSessions((int) workSessions)
                .totalBreakSessions((int) (totalSessions - workSessions))
                .build();
    }

    @Override
    @Transactional
    public WorkSession start(WorkSession s) {
        if (s.getStartTime() == null) s.setStartTime(LocalDateTime.now());
        s.setStatus(SessionStatus.ACTIVE);
        return workSessionRepository.save(s);
    }

    @Override
    @Transactional
    public WorkSession pause(Long id) {
        WorkSession ws = workSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + id));
        ws.pauseSession();
        return ws;
    }

    @Override
    @Transactional
    public WorkSession complete(Long id) {
        WorkSession ws = workSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + id));
        ws.completeSession();
        return ws;
    }

    @Override
    public List<WorkSession> listByRange(String userId, LocalDateTime from, LocalDateTime to) {
        return workSessionRepository.findByUserIdAndStartTimeBetween(userId, from, to);
    }

    @Override
    public List<WorkSession> listByStatus(String userId, SessionStatus status) {
        return workSessionRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<WorkSession> listByTask(String taskId) {
        return workSessionRepository.findByTaskIdOrderByStartTimeDesc(taskId);
    }
}