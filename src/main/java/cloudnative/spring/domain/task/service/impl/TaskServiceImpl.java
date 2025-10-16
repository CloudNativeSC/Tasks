package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.client.AiRecommendationClient;
import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import cloudnative.spring.domain.task.dto.request.Ai.AiRecommendationRequest;
import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatsEnhancedResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.dto.response.TimeSlotResponse;
import cloudnative.spring.domain.task.dto.response.Ai.AiRecommendationResponse;
import cloudnative.spring.domain.task.dto.response.Ai.AiTaskRecommendationResponse;
import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.repository.WorkSessionRepository;
import cloudnative.spring.domain.task.service.TaskService;
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;
import cloudnative.spring.external.client.UserClient;
import cloudnative.spring.external.client.AuthClient;
import cloudnative.spring.external.client.AppointmentClient;
import cloudnative.spring.external.dto.appointment.CreateAppointmentRequest;
import cloudnative.spring.external.dto.appointment.AppointmentResponse;
import cloudnative.spring.external.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final AiRecommendationClient aiRecommendationClient;
    private final WorkSessionRepository workSessionRepository;
    private final UserClient userClient;
    private final AuthClient authClient;
    private final AppointmentClient appointmentClient;

    @Override
    @Transactional
    public TaskResponse createTask(String userId, CreateTaskRequest request) {
        log.info("작업 생성 시작 - userId: {}, title: {}", userId, request.getTitle());

        validateUser(userId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new GeneralHandler(ErrorCode.CATEGORY_NOT_FOUND));

        Task task = Task.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .estimatedPomodoros(request.getEstimatedPomodoros())
                .dueAt(request.getDueAt())
                .isRecurring(Boolean.TRUE.equals(request.getIsRecurring()))
                .recurringPattern(request.getRecurringPattern())
                .scheduledDate(request.getScheduledDate())
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledEndTime())
                .category(category)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("작업 생성 완료 - taskId: {}", savedTask.getId());

        syncToAppointmentIfScheduled(userId, savedTask);

        return TaskResponse.from(savedTask);
    }

    @Override
    public List<TaskResponse> getAllTasks(String userId) {
        log.debug("전체 작업 조회 - userId: {}", userId);
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        log.debug("조회된 작업 수: {}", tasks.size());
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByStatus(String userId, TaskStatus status) {
        log.debug("상태별 작업 조회 - userId: {}, status: {}", userId, status);
        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(String taskId) {
        log.debug("작업 상세 조회 - taskId: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("작업을 찾을 수 없음 - taskId: {}", taskId);
                    return new GeneralHandler(ErrorCode.TASK_NOT_FOUND);
                });
        return TaskResponse.from(task);
    }

    @Override
    @Transactional
    public TaskResponse completeTask(String taskId) {
        log.info("작업 완료 처리 - taskId: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));
        task.markAsCompleted();
        Task savedTask = taskRepository.save(task);
        log.info("작업 완료 처리 완료 - taskId: {}", taskId);
        return TaskResponse.from(savedTask);
    }

    @Override
    public List<TaskResponse> getTodayCompletedTasks(String userId) {
        log.debug("오늘 완료된 작업 조회 - userId: {}", userId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Task> tasks = taskRepository.findCompletedTasksBetween(userId, startOfDay, endOfDay);
        log.debug("오늘 완료된 작업 수: {}", tasks.size());
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getUrgentTasks(String userId) {
        log.debug("급한 작업 조회 - userId: {}", userId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(1);

        List<Task> tasks = taskRepository.findUrgentTasks(userId, now, until);
        log.debug("급한 작업 수: {}", tasks.size());
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatusResponse getTaskStats(String userId) {
        log.debug("작업 통계 조회 - userId: {}", userId);
        long totalTasks = taskRepository.countByUserId(userId);
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        long todoTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.TODO);

        return TaskStatusResponse.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .todoTasks(todoTasks)
                .completionRate(totalTasks > 0 ? (double) completedTasks / totalTasks : 0.0)
                .build();
    }

    @Override
    public TaskStatsEnhancedResponse getTaskStatsEnhanced(String userId) {
        log.info("확장 통계 조회 - userId: {}", userId);

        LocalDateTime startOfWeek = LocalDateTime.now()
                .with(DayOfWeek.MONDAY)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        Long totalTasks = taskRepository.countByUserId(userId);
        Long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        Long todoTasks = totalTasks - completedTasks;

        Double completionRate = totalTasks > 0
                ? (completedTasks * 100.0 / totalTasks)
                : 0.0;

        Long weekTotalTasks = taskRepository.countWeekTotalTasks(userId, startOfWeek);
        Long weekCompletedTasks = taskRepository.countWeekCompletedTasks(userId, startOfWeek);

        Double weekCompletionRate = weekTotalTasks > 0
                ? (weekCompletedTasks * 100.0 / weekTotalTasks)
                : 0.0;

        Long totalPomodoros = workSessionRepository.countCompletedPomodoros(userId);
        Long todayPomodoros = workSessionRepository.countTodayCompletedPomodoros(userId);
        Long weekPomodoros = workSessionRepository.countWeekCompletedPomodoros(userId, startOfWeek);
        Long monthPomodoros = workSessionRepository.countMonthCompletedPomodoros(userId);

        Long totalFocusMinutes = workSessionRepository.sumTotalFocusMinutes(userId);
        Long todayFocusMinutes = workSessionRepository.sumTodayFocusMinutes(userId);
        Long weekFocusMinutes = workSessionRepository.sumWeekFocusMinutes(userId, startOfWeek);
        Long monthFocusMinutes = workSessionRepository.sumMonthFocusMinutes(userId);

        log.info("통계 결과 - 전체: {}, 완료: {}, 완료율: {}%",
                totalTasks, completedTasks, round(completionRate));
        log.info("이번 주 - 전체: {}, 완료: {}, 완료율: {}%",
                weekTotalTasks, weekCompletedTasks, round(weekCompletionRate));
        log.info("뽀모도로 - 전체: {}, 오늘: {}, 이번 주: {}, 이번 달: {}",
                totalPomodoros, todayPomodoros, weekPomodoros, monthPomodoros);
        log.info("집중 시간(분) - 전체: {}, 오늘: {}, 이번 주: {}, 이번 달: {}",
                totalFocusMinutes, todayFocusMinutes, weekFocusMinutes, monthFocusMinutes);

        return TaskStatsEnhancedResponse.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .todoTasks(todoTasks)
                .completionRate(round(completionRate))
                .weekTotalTasks(weekTotalTasks)
                .weekCompletedTasks(weekCompletedTasks)
                .weekCompletionRate(round(weekCompletionRate))
                .totalPomodoros(defaultIfNull(totalPomodoros))
                .todayPomodoros(defaultIfNull(todayPomodoros))
                .weekPomodoros(defaultIfNull(weekPomodoros))
                .monthPomodoros(defaultIfNull(monthPomodoros))
                .totalFocusHours(convertMinutesToHours(totalFocusMinutes))
                .todayFocusHours(convertMinutesToHours(todayFocusMinutes))
                .weekFocusHours(convertMinutesToHours(weekFocusMinutes))
                .monthFocusHours(convertMinutesToHours(monthFocusMinutes))
                .build();
    }

    @Override
    @Transactional
    public TaskResponse scheduleTask(String taskId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("작업 스케줄 설정 - taskId: {}, startTime: {}, endTime: {}", taskId, startTime, endTime);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));

        task.setScheduledDate(startTime.toLocalDate());
        task.setScheduledStartTime(startTime.toLocalTime());
        task.setScheduledEndTime(endTime.toLocalTime());

        Task savedTask = taskRepository.save(task);
        log.info("작업 스케줄 설정 완료 - taskId: {}", taskId);

        syncToAppointmentIfScheduled(task.getUserId(), savedTask);

        return TaskResponse.from(savedTask);
    }

    @Override
    public List<TaskResponse> getScheduledTasksByDate(String userId, LocalDate date) {
        log.debug("날짜별 스케줄된 작업 조회 - userId: {}, date: {}", userId, date);

        List<Task> tasks = taskRepository.findByUserIdAndScheduledDateOrderByScheduledStartTimeAsc(userId, date);
        log.debug("스케줄된 작업 수: {}", tasks.size());

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotResponse> getAvailableTimeSlots(String userId, LocalDate date) {
        log.debug("빈 시간 슬롯 조회 - userId: {}, date: {}", userId, date);

        List<Task> scheduledTasks = taskRepository.findByUserIdAndScheduledDateOrderByScheduledStartTimeAsc(userId, date);

        List<TimeSlotResponse> availableSlots = new ArrayList<>();
        LocalTime currentTime = LocalTime.of(6, 0);
        LocalTime endOfDay = LocalTime.of(23, 59);

        for (Task task : scheduledTasks) {
            LocalTime taskStart = task.getScheduledStartTime();
            LocalTime taskEnd = task.getScheduledEndTime();

            if (currentTime.isBefore(taskStart)) {
                availableSlots.add(new TimeSlotResponse(currentTime, taskStart));
                log.debug("빈 시간 슬롯 발견 - {} ~ {}", currentTime, taskStart);
            }

            currentTime = taskEnd;
        }

        if (currentTime.isBefore(endOfDay)) {
            availableSlots.add(new TimeSlotResponse(currentTime, endOfDay));
            log.debug("마지막 빈 시간 슬롯 - {} ~ {}", currentTime, endOfDay);
        }

        log.debug("총 빈 시간 슬롯 수: {}", availableSlots.size());
        return availableSlots;
    }

    @Override
    public AiTaskRecommendationResponse getAiRecommendations(String userId, Integer availableMinutes) {
        log.info("AI 추천 요청 - userId: {}, availableMinutes: {}분", userId, availableMinutes);

        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();
        int currentWeekday = now.getDayOfWeek().getValue() - 1;

        log.debug("현재 시간 정보 - hour: {}, weekday: {}", currentHour, currentWeekday);

        AiRecommendationRequest aiRequest = AiRecommendationRequest.builder()
                .userId(parseUserId(userId))
                .availableMinutes(availableMinutes)
                .numRecommendations(10)
                .startHour(currentHour)
                .weekday(currentWeekday)
                .fillTime(false)
                .build();

        AiRecommendationResponse aiResponse;
        try {
            aiResponse = aiRecommendationClient.getRecommendations(aiRequest);
            log.info("FastAPI 응답 수신 - 추천 개수: {}, 남은 시간: {}분",
                    aiResponse.getRecommendations().size(),
                    aiResponse.getRemainingMinutes());
        } catch (Exception e) {
            log.error("AI 추천 서버 통신 실패 - userId: {}", userId, e);
            throw new RuntimeException("AI 추천 서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.", e);
        }

        List<AiTaskRecommendationResponse.RecommendedTask> recommendations =
                aiResponse.getRecommendations().stream()
                        .map(aiTask -> AiTaskRecommendationResponse.RecommendedTask.builder()
                                .taskName(aiTask.getTaskName())
                                .taskCategory(aiTask.getTaskCategory())
                                .taskDuration(aiTask.getTaskDuration())
                                .score(aiTask.getScore())
                                .tags(aiTask.getTags() != null ? aiTask.getTags() : List.of())
                                .build())
                        .collect(Collectors.toList());

        List<AiTaskRecommendationResponse.RecommendedTask> packedRecommendations =
                aiResponse.getPackedRecommendations().stream()
                        .map(aiTask -> AiTaskRecommendationResponse.RecommendedTask.builder()
                                .taskName(aiTask.getTaskName())
                                .taskCategory(aiTask.getTaskCategory())
                                .taskDuration(aiTask.getTaskDuration())
                                .score(aiTask.getScore())
                                .tags(aiTask.getTags() != null ? aiTask.getTags() : List.of())
                                .build())
                        .collect(Collectors.toList());

        log.info("AI 추천 완료 - 추천: {}개, 시간 채우기: {}개",
                recommendations.size(), packedRecommendations.size());

        return AiTaskRecommendationResponse.builder()
                .userId(aiResponse.getUserId())
                .availableMinutes(aiResponse.getAvailableMinutes())
                .remainingMinutes(aiResponse.getRemainingMinutes())
                .fillTime(aiResponse.getFillTime())
                .recommendations(recommendations)
                .packedRecommendations(packedRecommendations)
                .build();
    }

    private void validateUser(String userId) {
        try {
            Boolean userExists = userClient.checkUserExists(userId);
            if (Boolean.FALSE.equals(userExists)) {
                log.error("사용자를 찾을 수 없음 - userId: {}", userId);
                throw new GeneralHandler(ErrorCode.USER_NOT_FOUND);
            }
            log.debug("사용자 검증 완료 - userId: {}", userId);
        } catch (ExternalServiceException e) {
            log.warn("User 서비스 호출 실패, Task 생성은 진행합니다 - error: {}", e.getMessage());
        }
    }

    private void syncToAppointmentIfScheduled(String userId, Task task) {
        if (task.getScheduledDate() == null
                || task.getScheduledStartTime() == null
                || task.getScheduledEndTime() == null) {
            return;
        }

        try {
            CreateAppointmentRequest appointmentRequest = CreateAppointmentRequest.builder()
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .appointmentDate(task.getScheduledDate())
                    .startTime(task.getScheduledStartTime())
                    .endTime(task.getScheduledEndTime())
                    .location("Task from Task Service")
                    .build();

            AppointmentResponse response = appointmentClient.createAppointment(userId, appointmentRequest);
            log.info("Appointment 동기화 완료 - taskId: {}, appointmentId: {}",
                    task.getId(), response.getId());
        } catch (ExternalServiceException e) {
            log.error("Appointment 동기화 실패 - taskId: {}, error: {}", task.getId(), e.getMessage());
        }
    }

    private Integer parseUserId(String userId) {
        try {
            return Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            log.warn("userId 변환 실패, 0으로 대체 - userId: {}", userId);
            return 0;
        }
    }

    private Double convertMinutesToHours(Long minutes) {
        if (minutes == null || minutes == 0) {
            return 0.0;
        }
        double hours = minutes / 60.0;
        return Math.round(hours * 100.0) / 100.0;
    }

    private Double round(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.round(value * 100.0) / 100.0;
    }

    private Long defaultIfNull(Long value) {
        return value != null ? value : 0L;
    }
}