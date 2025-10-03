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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TaskResponse createTask(String userId, CreateTaskRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));

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
        return TaskResponse.from(savedTask);
    }

    @Override
    public List<TaskResponse> getAllTasks(String userId) {
        // 오타 수정: finsd -> find
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByStatus(String userId, TaskStatus status) {
        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없습니다: " + taskId));
        return TaskResponse.from(task);
    }

    @Override
    @Transactional
    public TaskResponse completeTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없습니다: " + taskId));
        task.markAsCompleted();
        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Override
    public List<TaskResponse> getTodayCompletedTasks(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Repository 메서드는 프로젝트에 맞춰 구현되어 있어야 합니다.
        // 예: findCompletedTasksBetween(userId, startOfDay, endOfDay)
        List<Task> tasks = taskRepository.findCompletedTasksBetween(userId, startOfDay, endOfDay);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getUrgentTasks(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(1);

        // 예: 마감이 24시간 내인 작업
        List<Task> tasks = taskRepository.findUrgentTasks(userId, now, until);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatusResponse getTaskStats(String userId) {
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
