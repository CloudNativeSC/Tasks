package cloudnative.spring.domain.task.controller;

import cloudnative.spring.domain.task.dto.request.CreateTaskTagRequest;
import cloudnative.spring.domain.task.dto.response.TaskTagResponse;
import cloudnative.spring.domain.task.service.TaskTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-tags")
@RequiredArgsConstructor
@Tag(name = "TaskTag", description = "할 일 태그 관리 API")
public class TaskTagController {

    private final TaskTagService taskTagService;

    // 태그 생성 POST /api/task-tags
    @Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다.")
    @PostMapping
    public ResponseEntity<TaskTagResponse> createTaskTag(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "태그 생성 정보")
            @RequestBody CreateTaskTagRequest request) {
        TaskTagResponse tag = taskTagService.createTaskTag(userId, request);
        return ResponseEntity.status(201).body(tag);
    }

    // 사용자 태그 목록 조회 GET /api/task-tags?userId=123
    @Operation(summary = "태그 목록 조회", description = "사용자의 모든 태그를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TaskTagResponse>> getTaskTags(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<TaskTagResponse> tags = taskTagService.getTaskTagsByUserId(userId);
        return ResponseEntity.ok(tags);
    }

    // 태그 상세 조회 GET /api/task-tags/{id}
    @Operation(summary = "태그 상세 조회", description = "특정 태그의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskTagResponse> getTaskTagById(
            @Parameter(description = "태그 ID", required = true) @PathVariable String id) {
        TaskTagResponse tag = taskTagService.getTaskTagById(id);
        return ResponseEntity.ok(tag);
    }

    // 태그 수정 PUT /api/task-tags/{id}
    @Operation(summary = "태그 수정", description = "태그 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<TaskTagResponse> updateTaskTag(
            @Parameter(description = "태그 ID", required = true) @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "태그 수정 정보")
            @RequestBody CreateTaskTagRequest request) {
        TaskTagResponse tag = taskTagService.updateTaskTag(id, request);
        return ResponseEntity.ok(tag);
    }

    // 태그 삭제 DELETE /api/task-tags/{id}
    @Operation(summary = "태그 삭제", description = "태그를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskTag(
            @Parameter(description = "태그 ID", required = true) @PathVariable String id) {
        taskTagService.deleteTaskTag(id);
        return ResponseEntity.noContent().build();
    }
}