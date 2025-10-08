package cloudnative.spring.domain.task.controller;

import cloudnative.spring.domain.task.dto.request.CreateReflectionRequest;
import cloudnative.spring.domain.task.dto.request.UpdateReflectionRequest;
import cloudnative.spring.domain.task.dto.response.TaskReflectionResponse;
import cloudnative.spring.domain.task.service.TaskReflectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reflections")
@RequiredArgsConstructor
@Tag(name = "TaskReflection", description = "작업 회고 API (집중 모드)")
public class TaskReflectionController {

    private final TaskReflectionService taskReflectionService;

    // 워밍업 회고 생성
    @Operation(summary = "워밍업 회고 생성", description = "작업 시작 전 다짐을 작성합니다.")
    @PostMapping("/warmup")
    public ResponseEntity<TaskReflectionResponse> createWarmup(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "워밍업 회고 내용")
            @jakarta.validation.Valid @RequestBody CreateReflectionRequest request) {
        TaskReflectionResponse reflection = taskReflectionService.createWarmup(userId, request);
        return ResponseEntity.status(201).body(reflection);
    }

    // 쿨다운 회고 추가
    @Operation(summary = "쿨다운 회고 추가", description = "작업 완료 후 후기를 작성합니다.")
    @PatchMapping("/{id}/cooldown")
    public ResponseEntity<TaskReflectionResponse> addCooldown(
            @Parameter(description = "회고 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "쿨다운 회고 내용")
            @jakarta.validation.Valid @RequestBody UpdateReflectionRequest request) {
        TaskReflectionResponse reflection = taskReflectionService.addCooldown(id, request);
        return ResponseEntity.ok(reflection);
    }

    // 회고 목록 조회
    @Operation(summary = "회고 목록 조회", description = "사용자의 모든 회고를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TaskReflectionResponse>> getReflections(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<TaskReflectionResponse> reflections = taskReflectionService.getReflections(userId);
        return ResponseEntity.ok(reflections);
    }

    // 카테고리별 회고 조회
    @Operation(summary = "카테고리별 회고 조회", description = "특정 카테고리의 회고를 모아봅니다.")
    @GetMapping("/by-category")
    public ResponseEntity<List<TaskReflectionResponse>> getReflectionsByCategory(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "카테고리 이름", required = true) @RequestParam String categoryName) {
        List<TaskReflectionResponse> reflections = taskReflectionService.getReflectionsByCategory(userId, categoryName);
        return ResponseEntity.ok(reflections);
    }

    // 특정 회고 조회
    @Operation(summary = "회고 상세 조회", description = "특정 회고의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskReflectionResponse> getReflectionById(
            @Parameter(description = "회고 ID", required = true) @PathVariable Long id) {
        TaskReflectionResponse reflection = taskReflectionService.getReflectionById(id);
        return ResponseEntity.ok(reflection);
    }

    // 미완성 회고 조회
    @Operation(summary = "미완성 회고 조회", description = "쿨다운이 작성되지 않은 회고를 조회합니다.")
    @GetMapping("/incomplete")
    public ResponseEntity<List<TaskReflectionResponse>> getIncompleteReflections(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<TaskReflectionResponse> reflections = taskReflectionService.getIncompleteReflections(userId);
        return ResponseEntity.ok(reflections);
    }

    // 회고 삭제
    @Operation(summary = "회고 삭제", description = "작업 회고를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReflection(
            @Parameter(description = "회고 ID", required = true) @PathVariable Long id,
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        taskReflectionService.deleteReflection(id, userId);
        return ResponseEntity.noContent().build();
    }
}