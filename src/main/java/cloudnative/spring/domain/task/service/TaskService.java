package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import cloudnative.spring.domain.task.dto.request.UpdateTaskRequest;
import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatsEnhancedResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.dto.response.TimeAdjustment.TimeAdjustmentResponse;
import cloudnative.spring.domain.task.dto.response.TimeSlotResponse;
import cloudnative.spring.domain.task.dto.response.Ai.AiTaskRecommendationResponse;
import cloudnative.spring.domain.task.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
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

    // 타임라인 스케줄링
    TaskResponse scheduleTask(String taskId, LocalDateTime startTime, LocalDateTime endTime);
    List<TaskResponse> getScheduledTasksByDate(String userId, LocalDate date);
    List<TimeSlotResponse> getAvailableTimeSlots(String userId, LocalDate date);

    /**
     * AI 기반 Task 추천
     * @param userId 사용자 ID
     * @param availableMinutes 사용 가능한 시간(분)
     * @return 추천 Task 리스트 (점수순)
     */
    AiTaskRecommendationResponse getAiRecommendations(String userId, Integer availableMinutes);


    TaskStatsEnhancedResponse getTaskStatsEnhanced(String userId);

    /**
     * Task 수정
     */
    TaskResponse updateTask(String taskId, UpdateTaskRequest request);

    /**
     * Task 삭제
     */
    void deleteTask(String taskId);

    /**
     * 사용자의 시간 보정률 계산
     * - 예상 시간 vs 실제 시간 분석
     * - 전체 평균 보정률
     * - 카테고리별 보정률
     *
     * @param userId 사용자 ID
     * @return 시간 보정률 분석 결과
     */
    TimeAdjustmentResponse calculateTimeAdjustment(String userId);
}

