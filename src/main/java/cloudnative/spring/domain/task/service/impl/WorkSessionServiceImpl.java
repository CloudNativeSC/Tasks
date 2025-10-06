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
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSessionServiceImpl implements WorkSessionService {

    private final WorkSessionRepository workSessionRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public WorkSessionResponse startWorkSession(String userId, StartWorkSessionRequest request) {
        log.info("작업 세션 시작 - userId: {}, taskId: {}, type: {}",
                userId, request.getTaskId(), request.getSessionType());

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));

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
        log.info("작업 세션 시작 완료 - sessionId: {}", savedSession.getId());
        return WorkSessionResponse.from(savedSession);
    }

    @Override
    @Transactional
    public WorkSessionResponse endWorkSession(String id) {
        log.info("작업 세션 종료 - sessionId: {}", id);
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new GeneralHandler(ErrorCode.SESSION_NOT_FOUND));

        session.completeSession();
        log.info("작업 세션 종료 완료 - sessionId: {}", id);
        return WorkSessionResponse.from(session);
    }

    @Override
    @Transactional
    public WorkSessionResponse pauseWorkSession(String id) {
        log.info("작업 세션 일시정지 - sessionId: {}", id);
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new GeneralHandler(ErrorCode.SESSION_NOT_FOUND));

        session.pauseSession();
        return WorkSessionResponse.from(session);
    }

    @Override
    @Transactional
    public WorkSessionResponse resumeWorkSession(String id) {
        log.info("작업 세션 재개 - sessionId: {}", id);
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new GeneralHandler(ErrorCode.SESSION_NOT_FOUND));

        session.setStatus(SessionStatus.ACTIVE);
        return WorkSessionResponse.from(session);
    }

    @Override
    public List<WorkSessionResponse> getWorkSessionsByUserId(String userId) {
        log.debug("사용자 세션 목록 조회 - userId: {}", userId);
        List<WorkSession> sessions = workSessionRepository.findByUserId(userId);
        return sessions.stream()
                .map(WorkSessionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkSessionResponse> getWorkSessionsByDate(String userId, LocalDate date) {
        log.debug("날짜별 세션 조회 - userId: {}, date: {}", userId, date);
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
        log.debug("세션 상세 조회 - sessionId: {}", id);
        WorkSession session = workSessionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> {
                    log.error("세션을 찾을 수 없음 - sessionId: {}", id);
                    return new GeneralHandler(ErrorCode.SESSION_NOT_FOUND);
                });

        return WorkSessionResponse.from(session);
    }

    @Override
    public WorkSessionStatsResponse getWorkSessionStats(String userId, LocalDate startDate, LocalDate endDate) {
        log.debug("세션 통계 조회 - userId: {}, startDate: {}, endDate: {}", userId, startDate, endDate);
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
        log.debug("세션 시작 처리 - sessionId: {}", s.getId());
        if (s.getStartTime() == null) s.setStartTime(LocalDateTime.now());
        s.setStatus(SessionStatus.ACTIVE);
        return workSessionRepository.save(s);
    }

    @Override
    @Transactional
    public WorkSession pause(Long id) {
        log.debug("세션 일시정지 처리 - sessionId: {}", id);
        WorkSession ws = workSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + id));
        ws.pauseSession();
        return ws;
    }

    @Override
    @Transactional
    public WorkSession complete(Long id) {
        log.debug("세션 완료 처리 - sessionId: {}", id);
        WorkSession ws = workSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + id));
        ws.completeSession();
        return ws;
    }

    @Override
    public List<WorkSession> listByRange(String userId, LocalDateTime from, LocalDateTime to) {
        log.debug("기간별 세션 조회 - userId: {}, from: {}, to: {}", userId, from, to);
        return workSessionRepository.findByUserIdAndStartTimeBetween(userId, from, to);
    }

    @Override
    public List<WorkSession> listByStatus(String userId, SessionStatus status) {
        log.debug("상태별 세션 조회 - userId: {}, status: {}", userId, status);
        return workSessionRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<WorkSession> listByTask(String taskId) {
        log.debug("작업별 세션 조회 - taskId: {}", taskId);
        return workSessionRepository.findByTaskIdOrderByStartTimeDesc(taskId);
    }
}