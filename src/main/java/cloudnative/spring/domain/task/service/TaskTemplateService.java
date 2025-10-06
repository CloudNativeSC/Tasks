package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.CreateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.request.UpdateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.response.TaskTemplateResponse;
import cloudnative.spring.domain.task.enums.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskTemplateService {
    // 템플릿 CRUD
    TaskTemplateResponse createTaskTemplate(String userId, CreateTaskTemplateRequest request);
    TaskTemplateResponse updateTaskTemplate(String id, UpdateTaskTemplateRequest request);  // ← 이것만 남기기
    Page<TaskTemplateResponse> getTaskTemplatesByUserId(String userId, Pageable pageable);
    TaskTemplateResponse getTaskTemplateById(String id);
    void deleteTaskTemplate(String id);

    // 템플릿 활용
    void createTaskFromTemplate(String templateId, String userId);

    // 템플릿 검색
    Page<TaskTemplateResponse> searchByCategoryAndType(String categoryId, TemplateType type, Pageable pageable);
}