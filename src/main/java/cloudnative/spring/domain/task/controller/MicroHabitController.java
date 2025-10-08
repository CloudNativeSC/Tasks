package cloudnative.spring.domain.task.controller;

import cloudnative.spring.domain.task.dto.request.CreateMicroHabitRequest;
import cloudnative.spring.domain.task.dto.response.MicroHabitCompletionResponse;
import cloudnative.spring.domain.task.dto.response.MicroHabitResponse;
import cloudnative.spring.domain.task.service.MicroHabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/micro-habits")
@RequiredArgsConstructor
@Tag(name = "MicroHabit", description = "작은 습관 관리 API (동행 모드)")
public class MicroHabitController {

    private final MicroHabitService microHabitService;

    // 작은 습관 생성
    @Operation(summary = "작은 습관 생성", description = "새로운 작은 습관을 생성합니다.")
    @PostMapping
    public ResponseEntity<MicroHabitResponse> createMicroHabit(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "습관 생성 정보")
            @jakarta.validation.Valid @RequestBody CreateMicroHabitRequest request) {
        MicroHabitResponse habit = microHabitService.createMicroHabit(userId, request);
        return ResponseEntity.status(201).body(habit);
    }

    // 습관 목록 조회
    @Operation(summary = "습관 목록 조회", description = "사용자의 모든 작은 습관을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MicroHabitResponse>> getMicroHabits(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<MicroHabitResponse> habits = microHabitService.getMicroHabits(userId);
        return ResponseEntity.ok(habits);
    }

    // 오늘 추천 습관
    @Operation(summary = "오늘 추천 습관", description = "오늘 수행할 습관을 랜덤으로 추천합니다.")
    @GetMapping("/today")
    public ResponseEntity<MicroHabitResponse> getTodayRecommendedHabit(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        MicroHabitResponse habit = microHabitService.getTodayRecommendedHabit(userId);
        return ResponseEntity.ok(habit);
    }

    // 습관 완료 처리
    @Operation(summary = "습관 완료 처리", description = "습관을 완료하고 응원 메시지를 받습니다.")
    @PostMapping("/{id}/complete")
    public ResponseEntity<MicroHabitCompletionResponse> completeMicroHabit(
            @Parameter(description = "습관 ID", required = true) @PathVariable Long id,
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        MicroHabitCompletionResponse response = microHabitService.completeMicroHabit(id, userId);
        return ResponseEntity.ok(response);
    }

    // 습관 삭제
    @Operation(summary = "습관 삭제", description = "작은 습관을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMicroHabit(
            @Parameter(description = "습관 ID", required = true) @PathVariable Long id,
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        microHabitService.deleteMicroHabit(id, userId);
        return ResponseEntity.noContent().build();
    }
}