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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
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
        log.info("템플릿 생성 시작 - userId: {}, templateName: {}", userId, request.getTemplateName());

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
        log.info("템플릿 생성 완료 - templateId: {}", savedTemplate.getId());
        return TaskTemplateResponse.from(savedTemplate);
    }

    @Override
    public Page<TaskTemplateResponse> getTaskTemplatesByUserId(String userId, Pageable pageable) {
        log.debug("사용자 템플릿 목록 조회 - userId: {}", userId);
        Page<TaskTemplate> templates = taskTemplateRepository.findByUserIdOrderByUsageCountDesc(userId, pageable);
        return templates.map(TaskTemplateResponse::from);
    }

    @Override
    public TaskTemplateResponse getTaskTemplateById(String id) {
        log.debug("템플릿 상세 조회 - templateId: {}", id);
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("템플릿을 찾을 수 없음 - templateId: {}", id);
                    return new GeneralHandler(ErrorCode.TEMPLATE_NOT_FOUND);
                });
        return TaskTemplateResponse.from(template);
    }

    @Override
    @Transactional
    public TaskTemplateResponse updateTaskTemplate(String id, UpdateTaskTemplateRequest request) {
        log.info("템플릿 수정 시작 - templateId: {}", id);

        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TEMPLATE_NOT_FOUND));

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

        log.info("템플릿 수정 완료 - templateId: {}", id);
        return TaskTemplateResponse.from(template);
    }

    @Override
    @Transactional
    public void deleteTaskTemplate(String id) {
        log.info("템플릿 삭제 - templateId: {}", id);
        taskTemplateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void createTaskFromTemplate(String templateId, String userId) {
        log.info("템플릿으로부터 작업 생성 - templateId: {}, userId: {}", templateId, userId);

        TaskTemplate template = taskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TEMPLATE_NOT_FOUND));

        template.incrementUsage();

        Task task = template.createTaskFromTemplate(userId, UUID.randomUUID().toString());
        taskRepository.save(task);

        log.info("템플릿으로부터 작업 생성 완료 - taskId: {}", task.getId());
    }

    @Override
    public Page<TaskTemplateResponse> searchByCategoryAndType(String categoryId, TemplateType type, Pageable pageable) {
        log.debug("템플릿 검색 - categoryId: {}, type: {}", categoryId, type);
        Page<TaskTemplate> templates = taskTemplateRepository.findByCategoryIdAndTemplateType(categoryId, type, pageable);
        return templates.map(TaskTemplateResponse::from);
    }
}