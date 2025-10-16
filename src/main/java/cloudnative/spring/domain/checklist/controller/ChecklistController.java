package cloudnative.spring.domain.checklist.controller;

import cloudnative.spring.domain.checklist.dto.request.CreateChecklistRequest;
import cloudnative.spring.domain.checklist.dto.response.ChecklistTemplateResponse;
import cloudnative.spring.domain.checklist.dto.response.UserChecklistResponse;
import cloudnative.spring.domain.checklist.entity.ChecklistItem;
import cloudnative.spring.domain.checklist.service.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
@Tag(name = "Checklist", description = "체크리스트 관리 API")
public class ChecklistController {

    private final ChecklistService checklistService;

    /**
     * 템플릿 목록 조회
     */
    @Operation(summary = "체크리스트 템플릿 목록 조회",
            description = "시스템에서 제공하는 기본 체크리스트 템플릿 목록을 조회합니다.")
    @GetMapping("/templates")
    public ResponseEntity<List<ChecklistTemplateResponse>> getAllTemplates() {
        List<ChecklistTemplateResponse> templates = checklistService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * 템플릿 상세 조회
     */
    @Operation(summary = "체크리스트 템플릿 상세 조회",
            description = "특정 템플릿의 상세 정보와 항목 목록을 조회합니다.")
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ChecklistTemplateResponse> getTemplateById(
            @Parameter(description = "템플릿 ID", required = true)
            @PathVariable String templateId) {
        ChecklistTemplateResponse template = checklistService.getTemplateById(templateId);
        return ResponseEntity.ok(template);
    }

    /**
     * 체크리스트 생성
     */
    @Operation(summary = "체크리스트 생성",
            description = "템플릿을 기반으로 새로운 체크리스트를 생성합니다.")
    @PostMapping
    public ResponseEntity<UserChecklistResponse> createChecklist(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam String userId,
            @Parameter(description = "템플릿 ID", required = true)
            @RequestParam String templateId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "체크리스트 생성 정보")
            @RequestBody CreateChecklistRequest request) {
        UserChecklistResponse checklist = checklistService.createChecklistFromTemplate(userId, templateId, request);
        return ResponseEntity.status(201).body(checklist);
    }

    /**
     * 내 체크리스트 목록 조회
     */
    @Operation(summary = "내 체크리스트 목록 조회",
            description = "사용자의 체크리스트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserChecklistResponse>> getUserChecklists(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam String userId) {
        List<UserChecklistResponse> checklists = checklistService.getUserChecklists(userId);
        return ResponseEntity.ok(checklists);
    }

    /**
     * 체크리스트 상세 조회
     */
    @Operation(summary = "체크리스트 상세 조회",
            description = "특정 체크리스트의 상세 정보와 항목 목록을 조회합니다.")
    @GetMapping("/{checklistId}")
    public ResponseEntity<UserChecklistResponse> getChecklistById(
            @Parameter(description = "체크리스트 ID", required = true)
            @PathVariable String checklistId) {
        UserChecklistResponse checklist = checklistService.getChecklistById(checklistId);
        return ResponseEntity.ok(checklist);
    }

    /**
     * 체크리스트 항목 토글
     */
    @Operation(summary = "체크리스트 항목 토글",
            description = "체크리스트 항목을 체크하거나 체크 해제합니다.")
    @PatchMapping("/items/{itemId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleItem(
            @Parameter(description = "항목 ID", required = true)
            @PathVariable String itemId) {
        ChecklistItem item = checklistService.toggleItem(itemId);

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("content", item.getContent());
        response.put("isChecked", item.getIsChecked());
        response.put("checkedAt", item.getCheckedAt());
        response.put("displayOrder", item.getDisplayOrder());

        return ResponseEntity.ok(response);
    }

    /**
     * 체크리스트 삭제
     */
    @Operation(summary = "체크리스트 삭제",
            description = "체크리스트와 모든 항목을 삭제합니다.")
    @DeleteMapping("/{checklistId}")
    public ResponseEntity<Void> deleteChecklist(
            @Parameter(description = "체크리스트 ID", required = true)
            @PathVariable String checklistId) {
        checklistService.deleteChecklist(checklistId);
        return ResponseEntity.noContent().build();
    }
}