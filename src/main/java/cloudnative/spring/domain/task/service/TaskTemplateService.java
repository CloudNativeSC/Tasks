package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.CreateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.response.TaskTemplateResponse;
import cloudnative.spring.domain.task.enums.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskTemplateService {
    // 템플릿 CRUD
    TaskTemplateResponse createTaskTemplate(String userId, CreateTaskTemplateRequest request);
    Page<TaskTemplateResponse> getTaskTemplatesByUserId(String userId, Pageable pageable);
    TaskTemplateResponse getTaskTemplateById(String id);
    TaskTemplateResponse updateTaskTemplate(String id, CreateTaskTemplateRequest request);
    void deleteTaskTemplate(String id);

    // 템플릿 활용
    void createTaskFromTemplate(String templateId, String userId);

    // 템플릿 검색
    Page<TaskTemplateResponse> searchByCategoryAndType(String categoryId, TemplateType type, Pageable pageable);
}