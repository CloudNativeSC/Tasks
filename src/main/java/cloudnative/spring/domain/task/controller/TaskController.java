package cloudnative.spring.domain.task.controller;

import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import cloudnative.spring.domain.task.dto.response.Ai.AiTaskRecommendationResponse;
import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatsEnhancedResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.dto.response.TimeSlotResponse;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.service.TaskService;
import cloudnative.spring.domain.task.scheduler.RecurringTaskScheduler;  // ← 추가!
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // ← 추가!
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j  // ← 추가! (log 사용을 위해)
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "할 일 관리 API")
public class TaskController {

    private final TaskService taskService;
    private final RecurringTaskScheduler recurringTaskScheduler;  // ← 추가!

    // ========== 타임라인 스케줄링 ==========

    // 작업 스케줄 설정
    @Operation(summary = "작업 스케줄 설정", description = "작업을 특정 시간대에 배치합니다.")
    @PatchMapping("/{id}/schedule")
    public ResponseEntity<TaskResponse> scheduleTask(
            @Parameter(description = "작업 ID", required = true) @PathVariable String id,
            @Parameter(description = "시작 시간 (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간 (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        TaskResponse task = taskService.scheduleTask(id, startTime, endTime);
        return ResponseEntity.ok(task);
    }

    // 날짜별 스케줄된 작업 조회
    @Operation(summary = "날짜별 스케줄된 작업 조회", description = "특정 날짜에 스케줄된 작업 목록을 시간순으로 조회합니다.")
    @GetMapping("/scheduled")
    public ResponseEntity<List<TaskResponse>> getScheduledTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TaskResponse> tasks = taskService.getScheduledTasksByDate(userId, date);
        return ResponseEntity.ok(tasks);
    }

    // 빈 시간 슬롯 조회
    @Operation(summary = "빈 시간 슬롯 조회", description = "특정 날짜의 작업이 없는 시간대를 조회합니다.")
    @GetMapping("/available-slots")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableTimeSlots(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotResponse> slots = taskService.getAvailableTimeSlots(userId, date);
        return ResponseEntity.ok(slots);
    }

    // ========== 기존 작업 관리 ==========

    // 할 일 생성 POST /api/tasks
    @Operation(summary = "할 일 생성", description = "새로운 할 일을 생성합니다.")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "할 일 생성 정보")
            @jakarta.validation.Valid @RequestBody CreateTaskRequest request) {
        TaskResponse task = taskService.createTask(userId, request);
        return ResponseEntity.status(201).body(task);
    }

    // 할 일 목록 조회 GET /api/tasks?userId=abc&status=TODO
    @Operation(summary = "할 일 목록 조회", description = "사용자의 할 일 목록을 조회합니다. 상태별 필터링 가능합니다.")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "할 일 상태 (선택사항)") @RequestParam(required = false) TaskStatus status) {

        if (status != null) {
            List<TaskResponse> tasks = taskService.getTasksByStatus(userId, status);
            return ResponseEntity.ok(tasks);
        } else {
            List<TaskResponse> tasks = taskService.getAllTasks(userId);
            return ResponseEntity.ok(tasks);
        }
    }

    // 할 일 상세 조회 GET /api/tasks/{id}
    @Operation(summary = "할 일 상세 조회", description = "특정 할 일의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "할 일 ID", required = true) @PathVariable String id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // 할 일 완료 처리 PATCH /api/tasks/{id}/complete
    @Operation(summary = "할 일 완료 처리", description = "할 일을 완료 상태로 변경합니다.")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @Parameter(description = "할 일 ID", required = true) @PathVariable String id) {
        TaskResponse task = taskService.completeTask(id);
        return ResponseEntity.ok(task);
    }

    // 급한 할 일 조회 (24시간 이내 마감 등)
    @Operation(summary = "급한 할 일 조회 (AI 추천)", description = "AI가 추천하는 급한 할 일 목록을 조회합니다. (24시간 이내 마감, 우선순위 순)")
    @GetMapping("/urgent")
    public ResponseEntity<List<TaskResponse>> getUrgentTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<TaskResponse> tasks = taskService.getUrgentTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    // 오늘 완료된 할 일 조회 GET /api/tasks/today-completed?userId=abc
    @Operation(summary = "오늘 완료된 할 일 조회", description = "오늘 완료한 할 일 목록을 조회합니다.")
    @GetMapping("/today-completed")
    public ResponseEntity<List<TaskResponse>> getTodayCompletedTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<TaskResponse> tasks = taskService.getTodayCompletedTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    // 할 일 통계 조회 GET /api/tasks/stats?userId=abc
    @Operation(summary = "할 일 통계 조회", description = "사용자의 할 일 완료 통계를 조회합니다. (전체/완료/미완료 개수, 완료율)")
    @GetMapping("/stats")
    public ResponseEntity<TaskStatusResponse> getTaskStats(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        TaskStatusResponse stats = taskService.getTaskStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 확장된 통계 조회 (뽀모도로 + 집중 시간 포함)
     */
    @Operation(
            summary = "확장된 통계 조회 (마이페이지용)",
            description = "Task 통계 + 뽀모도로 통계 + 집중 시간 통계를 포함한 상세 통계"
    )
    @GetMapping("/stats/enhanced")
    public ResponseEntity<TaskStatsEnhancedResponse> getTaskStatsEnhanced(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        TaskStatsEnhancedResponse stats = taskService.getTaskStatsEnhanced(userId);
        return ResponseEntity.ok(stats);
    }


    // ========== AI 추천 ==========

    // AI 기반 할 일 추천
    @Operation(
            summary = "AI 추천 할 일",
            description = "LightGBM 모델 기반으로 템플릿 Task를 추천합니다. " +
                    "추천받은 Task를 선택하여 실제 할일로 등록할 수 있습니다."
    )
    @GetMapping("/ai-recommendations")
    public ResponseEntity<AiTaskRecommendationResponse> getAiRecommendations(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam String userId,
            @Parameter(description = "사용 가능한 시간(분)", required = true, example = "60")
            @RequestParam Integer availableMinutes) {

        if (availableMinutes <= 0) {
            throw new IllegalArgumentException("사용 가능한 시간은 0보다 커야 합니다.");
        }

        AiTaskRecommendationResponse response = taskService.getAiRecommendations(userId, availableMinutes);
        return ResponseEntity.ok(response);
    }

    // ========== 반복 작업 스케줄러 테스트 ==========

    /**
     * 반복 작업 수동 실행 (테스트/개발용)
     *
     * <p>매일 자정에 자동 실행되는 스케줄러를 수동으로 즉시 실행합니다.
     * <br>개발/테스트 환경에서 반복 Task가 제대로 생성되는지 확인할 때 사용하세요.
     *
     * <p><b>주의:</b> 운영 환경에서는 이 API를 비활성화하거나 권한을 제한하세요.
     *
     * @return 실행 결과 메시지
     */
    @Operation(
            summary = "반복 작업 수동 실행 (테스트용)",
            description = "스케줄러를 즉시 실행하여 반복 Task를 생성합니다. 개발/테스트 환경 전용입니다."
    )
    @PostMapping("/recurring/execute")
    public ResponseEntity<Map<String, String>> executeRecurringTasks() {
        log.info("반복 작업 수동 실행 요청");

        recurringTaskScheduler.createRecurringTasks();

        return ResponseEntity.ok(Map.of(
                "message", "반복 작업 실행 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}