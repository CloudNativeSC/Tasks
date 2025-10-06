package cloudnative.spring.domain.task.controller;

import cloudnative.spring.domain.task.dto.request.StartWorkSessionRequest;
import cloudnative.spring.domain.task.dto.response.WorkSessionResponse;
import cloudnative.spring.domain.task.dto.response.WorkSessionStatsResponse;
import cloudnative.spring.domain.task.service.WorkSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-sessions")
@RequiredArgsConstructor
@Tag(name = "WorkSession", description = "작업 세션 관리 API")
public class WorkSessionController {

    private final WorkSessionService workSessionService;

    // 작업 세션 시작 POST /api/work-sessions/start
    @Operation(summary = "작업 세션 시작", description = "새로운 작업 세션을 시작합니다.")
    @PostMapping("/start")
    public ResponseEntity<WorkSessionResponse> startWorkSession(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "작업 세션 시작 정보")
            @jakarta.validation.Valid @RequestBody StartWorkSessionRequest request) {
        WorkSessionResponse session = workSessionService.startWorkSession(userId, request);
        return ResponseEntity.status(201).body(session);
    }

    // 작업 세션 종료 PATCH /api/work-sessions/{id}/end
    @Operation(summary = "작업 세션 종료", description = "진행 중인 작업 세션을 종료합니다.")
    @PatchMapping("/{id}/end")
    public ResponseEntity<WorkSessionResponse> endWorkSession(
            @Parameter(description = "세션 ID", required = true) @PathVariable String id) {
        WorkSessionResponse session = workSessionService.endWorkSession(id);
        return ResponseEntity.ok(session);
    }

    // 작업 세션 일시정지 PATCH /api/work-sessions/{id}/pause
    @Operation(summary = "작업 세션 일시정지", description = "진행 중인 작업 세션을 일시정지합니다.")
    @PatchMapping("/{id}/pause")
    public ResponseEntity<WorkSessionResponse> pauseWorkSession(
            @Parameter(description = "세션 ID", required = true) @PathVariable String id) {
        WorkSessionResponse session = workSessionService.pauseWorkSession(id);
        return ResponseEntity.ok(session);
    }

    // 작업 세션 재개 PATCH /api/work-sessions/{id}/resume
    @Operation(summary = "작업 세션 재개", description = "일시정지된 작업 세션을 재개합니다.")
    @PatchMapping("/{id}/resume")
    public ResponseEntity<WorkSessionResponse> resumeWorkSession(
            @Parameter(description = "세션 ID", required = true) @PathVariable String id) {
        WorkSessionResponse session = workSessionService.resumeWorkSession(id);
        return ResponseEntity.ok(session);
    }

    // 사용자 작업 세션 목록 조회 GET /api/work-sessions?userId=123
    @Operation(summary = "작업 세션 목록 조회", description = "사용자의 모든 작업 세션을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WorkSessionResponse>> getWorkSessions(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<WorkSessionResponse> sessions = workSessionService.getWorkSessionsByUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    // 날짜별 작업 세션 조회 GET /api/work-sessions/by-date?userId=123&date=2025-10-01
    @Operation(summary = "날짜별 작업 세션 조회", description = "특정 날짜의 작업 세션을 조회합니다.")
    @GetMapping("/by-date")
    public ResponseEntity<List<WorkSessionResponse>> getWorkSessionsByDate(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", required = true) @RequestParam LocalDate date) {
        List<WorkSessionResponse> sessions = workSessionService.getWorkSessionsByDate(userId, date);
        return ResponseEntity.ok(sessions);
    }

    // 작업 세션 상세 조회 GET /api/work-sessions/{id}
    @Operation(summary = "작업 세션 상세 조회", description = "특정 작업 세션의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<WorkSessionResponse> getWorkSessionById(
            @Parameter(description = "세션 ID", required = true) @PathVariable String id) {
        WorkSessionResponse session = workSessionService.getWorkSessionById(id);
        return ResponseEntity.ok(session);
    }

    // 작업 세션 통계 조회 GET /api/work-sessions/stats?userId=123
    @Operation(summary = "작업 세션 통계 조회", description = "사용자의 작업 세션 통계를 조회합니다. (총 작업 시간, 평균 집중도 등)")
    @GetMapping("/stats")
    public ResponseEntity<WorkSessionStatsResponse> getWorkSessionStats(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "시작 날짜 (yyyy-MM-dd)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜 (yyyy-MM-dd)") @RequestParam(required = false) LocalDate endDate) {
        WorkSessionStatsResponse stats = workSessionService.getWorkSessionStats(userId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}