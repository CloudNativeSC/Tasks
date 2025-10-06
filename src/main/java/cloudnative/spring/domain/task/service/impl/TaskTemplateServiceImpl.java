package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.dto.request.CreateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.request.UpdateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.response.TaskTemplateResponse;
import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.entity.TaskTemplate;
import cloudnative.spring.domain.task.enums.TemplateType;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.repository.TaskTemplateRepository;
import cloudnative.spring.domain.task.service.TaskTemplateService;

import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;

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
                .orElseThrow(() -> new GeneralHandler(ErrorCode.CATEGORY_NOT_FOUND));

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
                .userId(userId)
                .usageCount(0)
                .build();

        TaskTemplate savedTemplate = taskTemplateRepository.save(template);
        return TaskTemplateResponse.from(savedTemplate);
    }

    @Override
    public Page<TaskTemplateResponse> getTaskTemplatesByUserId(String userId, Pageable pageable) {
        Page<TaskTemplate> templates = taskTemplateRepository.findByUserIdOrderByUsageCountDesc(userId, pageable);
        return templates.map(TaskTemplateResponse::from);
    }

    @Override
    public TaskTemplateResponse getTaskTemplateById(String id) {
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TEMPLATE_NOT_FOUND));
        return TaskTemplateResponse.from(template);
    }

    @Override
    @Transactional
    public TaskTemplateResponse updateTaskTemplate(String id, UpdateTaskTemplateRequest request) {
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TEMPLATE_NOT_FOUND));

        // null이 아닌 필드만 업데이트 (부분 업데이트 지원)
        if (request.getTemplateName() != null) {
            template.setTemplateName(request.getTemplateName());
        }
        if (request.getTitle() != null) {
            template.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getEstimatedPomodoros() != null) {
            template.setEstimatedPomodoros(request.getEstimatedPomodoros());
        }
        if (request.getTags() != null) {
            template.setTags(request.getTags());
        }
        if (request.getTemplateType() != null) {
            template.setTemplateType(request.getTemplateType());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new GeneralHandler(ErrorCode.CATEGORY_NOT_FOUND));
            template.setCategoryId(request.getCategoryId());
        }

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
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TEMPLATE_NOT_FOUND));

        template.incrementUsage();

        Task task = template.createTaskFromTemplate(userId, UUID.randomUUID().toString());
        taskRepository.save(task);
    }

    @Override
    public Page<TaskTemplateResponse> searchByCategoryAndType(String categoryId, TemplateType type, Pageable pageable) {
        Page<TaskTemplate> templates = taskTemplateRepository.findByCategoryIdAndTemplateType(categoryId, type, pageable);
        return templates.map(TaskTemplateResponse::from);
    }
}