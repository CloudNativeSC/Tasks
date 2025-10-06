package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.service.TaskService;
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public TaskResponse createTask(String userId, CreateTaskRequest request) {
        log.info("작업 생성 시작 - userId: {}, title: {}", userId, request.getTitle());

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
                .category(category)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("작업 생성 완료 - taskId: {}", savedTask.getId());
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
}