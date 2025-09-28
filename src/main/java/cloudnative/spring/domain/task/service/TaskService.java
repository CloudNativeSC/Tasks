package cloudnative.spring.domain.task.service;


import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;


    //할 일 생성

    @Transactional
    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        // Category 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));

        // Task 생성
        Task task = Task.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .estimatedPomodoros(request.getEstimatedPomodoros())
                .dueAt(request.getDueAt())
                .isRecurring(request.getIsRecurring())
                .category(category)
                .build();

        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    //사용자의 모든 할 일 조회
    public List<TaskResponse> getAllTasks(Long userId) {
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    //상태별 할 일 조회
    public List<TaskResponse> getTasksByStatus(Long userId, TaskStatus status) {
        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    //할 일 상세 조회
    public TaskResponse getTaskById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없습니다: " + taskId));

        return TaskResponse.from(task);
    }

    //할 일 완료 처리
    @Transactional // 데이터 변경이 있으므로 readOnly 제거
    public TaskResponse completeTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없습니다: " + taskId));

        // 비즈니스 로직: 완료 처리
        task.markAsCompleted();

        // 저장 (더티 체킹으로 자동 저장)
        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    //오늘 완료된 할 일 조회
    public List<TaskResponse> getTodayCompletedTasks(Long userId) {
        LocalDateTime today = LocalDateTime.now();
        List<Task> tasks = taskRepository.findCompletedTasksToday(userId, today);

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    //급한 할 일 조회_AI
    public List<TaskResponse> getUrgentTasks(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Task> tasks = taskRepository.findUrgentTasks(userId, now, tomorrow);

        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    //할 일 통계 조회
    public TaskStatusResponse getTaskStats(Long userId) {
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
