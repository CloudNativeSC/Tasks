package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.dto.request.CreateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.response.TaskTemplateResponse;
import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.entity.TaskTemplate;
import cloudnative.spring.domain.task.enums.TemplateType;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.repository.TaskTemplateRepository;
import cloudnative.spring.domain.task.service.TaskTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskTemplateServiceImpl implements TaskTemplateService {

    private final TaskTemplateRepository taskTemplateRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskTemplateResponse createTaskTemplate(String userId, CreateTaskTemplateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));

        TaskTemplate template = TaskTemplate.builder()
                .id(UUID.randomUUID().toString())
                .templateName(request.getTemplateName())
                .title(request.getTitle())
                .description(request.getDescription())
                .estimatedPomodoros(request.getEstimatedPomodoros())
                .tags(request.getTags())
                .isAiGenerated(request.getIsAiGenerated() != null ? request.getIsAiGenerated() : false)
                .templateType(request.getTemplateType() != null ? request.getTemplateType() : TemplateType.CUSTOM)
                .categoryId(request.getCategoryId())
                .userId(userId) // userId 저장
                .usageCount(0)
                .build();

        TaskTemplate savedTemplate = taskTemplateRepository.save(template);
        return TaskTemplateResponse.from(savedTemplate);
    }

    @Override
    public Page<TaskTemplateResponse> getTaskTemplatesByUserId(String userId, Pageable pageable) {
        // 보안 수정: userId로 필터링
        Page<TaskTemplate> templates = taskTemplateRepository.findByUserIdOrderByUsageCountDesc(userId, pageable);
        return templates.map(TaskTemplateResponse::from);
    }

    @Override
    public TaskTemplateResponse getTaskTemplateById(String id) {
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + id));
        return TaskTemplateResponse.from(template);
    }

    @Override
    @Transactional
    public TaskTemplateResponse updateTaskTemplate(String id, CreateTaskTemplateRequest request) {
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));

        // Entity의 업데이트 메서드로 변경 권장
        template.setTemplateName(request.getTemplateName());
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setEstimatedPomodoros(request.getEstimatedPomodoros());
        template.setTags(request.getTags());
        template.setTemplateType(request.getTemplateType());
        template.setCategoryId(request.getCategoryId());

        return TaskTemplateResponse.from(template);
    }

    @Override
    @Transactional
    public void deleteTaskTemplate(String id) {
        taskTemplateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void createTaskFromTemplate(String templateId, String userId) {
        TaskTemplate template = taskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + templateId));

        // 도메인 로직: 사용 횟수 증가
        template.incrementUsage();

        // 템플릿으로부터 Task 생성 (도메인 로직)
        Task task = template.createTaskFromTemplate(userId, UUID.randomUUID().toString());
        taskRepository.save(task);
    }

    @Override
    public Page<TaskTemplateResponse> searchByCategoryAndType(String categoryId, TemplateType type, Pageable pageable) {
        Page<TaskTemplate> templates = taskTemplateRepository.findByCategoryIdAndTemplateType(categoryId, type, pageable);
        return templates.map(TaskTemplateResponse::from);
    }
}