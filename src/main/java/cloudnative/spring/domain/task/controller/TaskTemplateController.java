package cloudnative.spring.domain.tasktemplate.controller;

import cloudnative.spring.domain.task.dto.request.CreateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.response.TaskTemplateResponse;
import cloudnative.spring.domain.task.service.TaskTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-templates")
@RequiredArgsConstructor
@Tag(name = "TaskTemplate", description = "할 일 템플릿 관리 API")
public class TaskTemplateController {

    private final TaskTemplateService taskTemplateService;

    // 템플릿 생성 POST /api/task-templates
    @Operation(summary = "템플릿 생성", description = "새로운 할 일 템플릿을 생성합니다.")
    @PostMapping
    public ResponseEntity<TaskTemplateResponse> createTaskTemplate(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "템플릿 생성 정보")
            @RequestBody CreateTaskTemplateRequest request) {
        TaskTemplateResponse template = taskTemplateService.createTaskTemplate(userId, request);
        return ResponseEntity.status(201).body(template);
    }

    // 사용자 템플릿 목록 조회 GET /api/task-templates?userId=123
    @Operation(summary = "템플릿 목록 조회", description = "사용자의 모든 템플릿을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TaskTemplateResponse>> getTaskTemplates(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<TaskTemplateResponse> templates = taskTemplateService.getTaskTemplatesByUserId(userId);
        return ResponseEntity.ok(templates);
    }

    // 템플릿 상세 조회 GET /api/task-templates/{id}
    @Operation(summary = "템플릿 상세 조회", description = "특정 템플릿의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskTemplateResponse> getTaskTemplateById(
            @Parameter(description = "템플릿 ID", required = true) @PathVariable String id) {
        TaskTemplateResponse template = taskTemplateService.getTaskTemplateById(id);
        return ResponseEntity.ok(template);
    }

    // 템플릿으로 할 일 생성 POST /api/task-templates/{id}/create-task
    @Operation(summary = "템플릿으로 할 일 생성", description = "템플릿을 기반으로 실제 할 일을 생성합니다.")
    @PostMapping("/{id}/create-task")
    public ResponseEntity<Void> createTaskFromTemplate(
            @Parameter(description = "템플릿 ID", required = true) @PathVariable String id,
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        taskTemplateService.createTaskFromTemplate(id, userId);
        return ResponseEntity.status(201).build();
    }

    // 템플릿 수정 PUT /api/task-templates/{id}
    @Operation(summary = "템플릿 수정", description = "템플릿 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<TaskTemplateResponse> updateTaskTemplate(
            @Parameter(description = "템플릿 ID", required = true) @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "템플릿 수정 정보")
            @RequestBody CreateTaskTemplateRequest request) {
        TaskTemplateResponse template = taskTemplateService.updateTaskTemplate(id, request);
        return ResponseEntity.ok(template);
    }

    // 템플릿 삭제 DELETE /api/task-templates/{id}
    @Operation(summary = "템플릿 삭제", description = "템플릿을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskTemplate(
            @Parameter(description = "템플릿 ID", required = true) @PathVariable String id) {
        taskTemplateService.deleteTaskTemplate(id);
        return ResponseEntity.noContent().build();
    }
}