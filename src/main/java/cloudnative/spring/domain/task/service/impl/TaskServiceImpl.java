package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.client.AiRecommendationClient;
import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import cloudnative.spring.domain.task.dto.request.Ai.AiRecommendationRequest;
import cloudnative.spring.domain.task.dto.request.UpdateTaskRequest;
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
import cloudnative.spring.domain.task.service.GoogleCalendarService;
import cloudnative.spring.domain.task.service.TaskService;
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;
import cloudnative.spring.external.client.UserClient;
import cloudnative.spring.external.client.AuthClient;
import cloudnative.spring.external.client.AppointmentClient;
import cloudnative.spring.external.dto.appointment.CreateAppointmentRequest;
import cloudnative.spring.external.dto.appointment.AppointmentResponse;
import cloudnative.spring.external.exception.ExternalServiceException;
import cloudnative.spring.domain.task.dto.response.TimeAdjustment.TimeAdjustmentResponse;

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
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
    private final GoogleCalendarService googleCalendarService;  // ← 추가

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

    // ========== 스케줄링 및 구글 캘린더 연동 ==========

    @Override
    @Transactional
    public TaskResponse scheduleTask(String taskId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("작업 스케줄 설정 - taskId: {}, startTime: {}, endTime: {}", taskId, startTime, endTime);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));

        task.setScheduledDate(startTime.toLocalDate());
        task.setScheduledStartTime(startTime.toLocalTime());
        task.setScheduledEndTime(endTime.toLocalTime());

        // 구글 캘린더 연동
        if (task.getUserId() != null) {
            try {
                String eventId = googleCalendarService.createCalendarEvent(
                        task.getUserId(),
                        task.getTitle(),
                        task.getDescription(),
                        startTime,
                        endTime
                );
                task.setGoogleCalendarEventId(eventId);
                log.info("✅ 구글 캘린더 연동 완료 - eventId: {}", eventId);
            } catch (Exception e) {
                log.warn("⚠️ 구글 캘린더 연동 실패 (Task는 저장됨)", e);
            }
        }

        Task savedTask = taskRepository.save(task);
        log.info("작업 스케줄 설정 완료 - taskId: {}", taskId);

        // Appointment 동기화
        syncToAppointmentIfScheduled(task.getUserId(), savedTask);

        return TaskResponse.from(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String taskId, UpdateTaskRequest request) {
        log.info("작업 수정 - taskId: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));

        // Task 정보 업데이트
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getEstimatedPomodoros() != null) {
            task.setEstimatedPomodoros(request.getEstimatedPomodoros());
        }
        if (request.getDueAt() != null) {
            task.setDueAt(request.getDueAt());
        }
        if (request.getScheduledDate() != null) {
            task.setScheduledDate(request.getScheduledDate());
        }
        if (request.getScheduledStartTime() != null) {
            task.setScheduledStartTime(request.getScheduledStartTime());
        }
        if (request.getScheduledEndTime() != null) {
            task.setScheduledEndTime(request.getScheduledEndTime());
        }

        // Category 업데이트
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new GeneralHandler(ErrorCode.CATEGORY_NOT_FOUND));
            task.setCategory(category);
        }

        // 구글 캘린더 이벤트 업데이트
        if (task.getGoogleCalendarEventId() != null
                && task.getUserId() != null
                && task.getScheduledDate() != null
                && task.getScheduledStartTime() != null
                && task.getScheduledEndTime() != null) {

            try {
                LocalDateTime startTime = LocalDateTime.of(
                        task.getScheduledDate(),
                        task.getScheduledStartTime()
                );
                LocalDateTime endTime = LocalDateTime.of(
                        task.getScheduledDate(),
                        task.getScheduledEndTime()
                );

                googleCalendarService.updateCalendarEvent(
                        task.getUserId(),
                        task.getGoogleCalendarEventId(),
                        task.getTitle(),
                        task.getDescription(),
                        startTime,
                        endTime
                );
                log.info("✅ 구글 캘린더 이벤트 수정 완료 - eventId: {}",
                        task.getGoogleCalendarEventId());
            } catch (Exception e) {
                log.warn("⚠️ 구글 캘린더 이벤트 수정 실패 - eventId: {}",
                        task.getGoogleCalendarEventId(), e);
            }
        }

        Task savedTask = taskRepository.save(task);
        log.info("작업 수정 완료 - taskId: {}", taskId);

        // Appointment 동기화
        syncToAppointmentIfScheduled(task.getUserId(), savedTask);

        return TaskResponse.from(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(String taskId) {
        log.info("작업 삭제 - taskId: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));

        // 1. WorkSession 삭제
        try {
            workSessionRepository.deleteByTaskId(taskId);
            log.info("✅ WorkSession 삭제 완료 - taskId: {}", taskId);
        } catch (Exception e) {
            log.warn("⚠️ WorkSession 삭제 실패 - taskId: {}", taskId, e);
        }

        // 2. 구글 캘린더 이벤트 삭제
        if (task.getGoogleCalendarEventId() != null && task.getUserId() != null) {
            try {
                googleCalendarService.deleteCalendarEvent(
                        task.getUserId(),
                        task.getGoogleCalendarEventId()
                );
                log.info("✅ 구글 캘린더 이벤트 삭제 완료 - eventId: {}",
                        task.getGoogleCalendarEventId());
            } catch (Exception e) {
                log.warn("⚠️ 구글 캘린더 이벤트 삭제 실패 - eventId: {}",
                        task.getGoogleCalendarEventId(), e);
            }
        }

        // 3. Task 삭제
        taskRepository.delete(task);
        log.info("작업 삭제 완료 - taskId: {}", taskId);
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

    // ========== AI 추천 ==========

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

    // ========== Private 헬퍼 메소드 ==========

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

    //시간 보정 기능 구현

    @Override
    public TimeAdjustmentResponse calculateTimeAdjustment(String userId) {
        log.info("시간 보정률 계산 시작 - userId: {}", userId);

        // 1. 완료된 Task 조회 (WorkSession + Category 포함)
        List<Task> completedTasks = taskRepository.findCompletedTasksWithSessions(userId);

        if (completedTasks.isEmpty()) {
            log.info("분석할 데이터 없음 - userId: {}", userId);
            return TimeAdjustmentResponse.builder()
                    .userId(userId)
                    .adjustmentRatio(1.0)
                    .categoryRatios(new HashMap<>())
                    .suggestion("아직 완료한 작업이 없습니다. 작업을 완료하면 분석이 시작됩니다.")
                    .analyzedTaskCount(0)
                    .totalEstimatedMinutes(0L)
                    .totalActualMinutes(0L)
                    .build();
        }

        // 2. 전체 평균 보정률 계산
        double totalRatio = 0.0;
        int validTaskCount = 0;
        long totalEstimated = 0L;
        long totalActual = 0L;

        for (Task task : completedTasks) {
            // 예상 시간 계산
            Long estimatedMinutes = calculateEstimatedMinutes(task);
            if (estimatedMinutes == null || estimatedMinutes == 0) {
                continue;
            }

            // 실제 시간 계산
            Integer actualMinutes = calculateActualMinutes(task);
            if (actualMinutes == 0) {
                continue;
            }

            // 보정률 = 실제 / 예상
            double ratio = (double) actualMinutes / estimatedMinutes;
            totalRatio += ratio;
            validTaskCount++;

            totalEstimated += estimatedMinutes;
            totalActual += actualMinutes;

            log.debug("Task 분석 - title: {}, 예상: {}분, 실제: {}분, 비율: {:.2f}",
                    task.getTitle(), estimatedMinutes, actualMinutes, ratio);
        }

        // 3. 평균 보정률
        double avgRatio = validTaskCount > 0 ? totalRatio / validTaskCount : 1.0;

        // 4. 카테고리별 보정률 계산
        Map<String, Double> categoryRatios = calculateCategoryRatios(completedTasks);

        log.info("시간 보정률 계산 완료 - userId: {}, 전체 보정률: {:.2f}, 분석 Task: {}개, 카테고리: {}개",
                userId, avgRatio, validTaskCount, categoryRatios.size());

        // 5. 응답 생성
        return TimeAdjustmentResponse.builder()
                .userId(userId)
                .adjustmentRatio(Math.round(avgRatio * 100.0) / 100.0)  // 소수점 2자리
                .categoryRatios(categoryRatios)
                .suggestion(generateSuggestion(avgRatio))
                .analyzedTaskCount(validTaskCount)
                .totalEstimatedMinutes(totalEstimated)
                .totalActualMinutes(totalActual)
                .build();
    }

    /**
     * 카테고리별 시간 보정률 계산
     */
    private Map<String, Double> calculateCategoryRatios(List<Task> tasks) {
        Map<String, List<Double>> ratiosByCategory = new HashMap<>();

        for (Task task : tasks) {
            // 카테고리명 추출
            String categoryName = task.getCategory() != null
                    ? task.getCategory().getName()
                    : "미분류";

            Long estimated = calculateEstimatedMinutes(task);
            Integer actual = calculateActualMinutes(task);

            if (estimated != null && estimated > 0 && actual > 0) {
                double ratio = (double) actual / estimated;
                ratiosByCategory
                        .computeIfAbsent(categoryName, k -> new ArrayList<>())
                        .add(ratio);
            }
        }

        // 각 카테고리별 평균 계산
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : ratiosByCategory.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(1.0);
            result.put(entry.getKey(), Math.round(avg * 100.0) / 100.0);  // 소수점 2자리

            log.debug("카테고리별 보정률 - {}: {:.2f}배 ({}개 Task 분석)",
                    entry.getKey(), avg, entry.getValue().size());
        }

        return result;
    }

    /**
     * 예상 소요 시간 계산 (분)
     */
    private Long calculateEstimatedMinutes(Task task) {
        if (task.getScheduledStartTime() == null || task.getScheduledEndTime() == null) {
            return null;
        }

        LocalTime start = task.getScheduledStartTime();
        LocalTime end = task.getScheduledEndTime();

        return Duration.between(start, end).toMinutes();
    }

    /**
     * 실제 소요 시간 계산 (분)
     * WorkSession의 durationMinutes 합산
     */
    private Integer calculateActualMinutes(Task task) {
        if (task.getWorkSessions() == null || task.getWorkSessions().isEmpty()) {
            return 0;
        }

        return task.getWorkSessions().stream()
                .mapToInt(ws -> ws.getDurationMinutes() != null ? ws.getDurationMinutes() : 0)
                .sum();
    }

    /**
     * 보정률에 따른 제안 메시지 생성
     */
    private String generateSuggestion(Double ratio) {
        if (ratio < 0.8) {
            return "예상보다 빨리 끝내셨어요! 시간을 좀 더 여유롭게 잡아도 좋겠어요.";
        } else if (ratio < 1.2) {
            return "예상 시간이 정확해요! 지금처럼 계획하면 해낼 수 있을 거에요.";
        } else if (ratio < 1.5) {
            int percentage = (int) Math.round(ratio * 100);
            return String.format("평균적으로 예상보다 조금 더 걸려요시간을 %d%%로 계획하는 건 어떨까요?", percentage);
        } else {
            return "예상보다 많이 걸리는 편이에요.시간을 1.5배 정도 여유있게 잡으시길 추천해요.";
        }
    }
}

