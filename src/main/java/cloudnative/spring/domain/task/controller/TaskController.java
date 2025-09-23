package cloudnative.spring.domain.task.controller;

import cloudnative.spring.domain.task.dto.response.TaskResponse;
import cloudnative.spring.domain.task.dto.response.TaskStatusResponse;
import cloudnative.spring.domain.task.enums.TaskStatus;
import cloudnative.spring.domain.task.service.TaskService;
import cloudnative.spring.domain.task.dto.request.CreateTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "할 일 관리 API")
public class TaskController {

    private final TaskService taskService;

    //할 일 생성 POST /api/tasks
    @Operation(summary = "할 일 생성", description = "새로운 할 일을 생성합니다.")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "할 일 생성 정보")
            @RequestBody CreateTaskRequest request) {
        TaskResponse task = taskService.createTask(userId, request);
        return ResponseEntity.status(201).body(task);
    }

    //할 일 목록 조회 GET/api/tasks?userId=123&status=TODO
    @Operation(summary = "할 일 목록 조회", description = "사용자의 할 일 목록을 조회합니다. 상태별 필터링 가능합니다.")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId,
            @Parameter(description = "할 일 상태 (선택사항)") @RequestParam(required = false) TaskStatus status) {

        if (status != null) {
            List<TaskResponse> tasks = taskService.getTasksByStatus(userId, status);
            return ResponseEntity.ok(tasks);
        } else {
            List<TaskResponse> tasks = taskService.getAllTasks(userId);
            return ResponseEntity.ok(tasks);
        }
    }

    //할 일 상세 조회 GET/api/tasks/{id}
    @Operation(summary = "할 일 상세 조회", description = "특정 할 일의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "할 일 ID", required = true) @PathVariable String id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    //할 일 완료 처리 PATCH /api/tasks/{id}/complete
    @Operation(summary = "할 일 완료 처리", description = "할 일을 완료 상태로 변경합니다.")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @Parameter(description = "할 일 ID", required = true) @PathVariable String id) {
        TaskResponse task = taskService.completeTask(id);
        return ResponseEntity.ok(task);
    }

    //급한 할 일 조회_AI(urgent 확인 필요)
    @Operation(summary = "급한 할 일 조회 (AI 추천)", description = "AI가 추천하는 급한 할 일 목록을 조회합니다. (24시간 이내 마감, 우선순위 순)")
    @GetMapping("/urgent")
    public ResponseEntity<List<TaskResponse>> getUrgentTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {
        List<TaskResponse> tasks = taskService.getUrgentTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    //오늘 완료된 할 일 조회 GET /api/tasks/today-completed?userId=123
    @Operation(summary = "오늘 완료된 할 일 조회", description = "오늘 완료한 할 일 목록을 조회합니다.")
    @GetMapping("/today-completed")
    public ResponseEntity<List<TaskResponse>> getTodayCompletedTasks(
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {
        List<TaskResponse> tasks = taskService.getTodayCompletedTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    //할 일 통계 조회 GET /api/tasks/stats?userId=123
    @Operation(summary = "할 일 통계 조회", description = "사용자의 할 일 완료 통계를 조회합니다. (전체/완료/미완료 개수, 완료율)")
    @GetMapping("/stats")
    public ResponseEntity<TaskStatusResponse> getTaskStats(
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {
        TaskStatusResponse stats = taskService.getTaskStats(userId);
        return ResponseEntity.ok(stats);
    }
}
