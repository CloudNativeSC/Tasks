package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.enums.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(String userId, CreateTaskRequest request);
    List<TaskResponse> getAllTasks(String userId);
    List<TaskResponse> getTasksByStatus(String userId, TaskStatus status);
    TaskResponse getTaskById(String taskId);
    TaskResponse completeTask(String taskId);
    List<TaskResponse> getTodayCompletedTasks(String userId);
    List<TaskResponse> getUrgentTasks(String userId);
    TaskStatusResponse getTaskStats(String userId);
}
