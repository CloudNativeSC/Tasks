package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.entity.Category;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.entity.TaskTemplate;
import cloudnative.spring.domain.task.enums.TemplateType;
import cloudnative.spring.domain.task.repository.CategoryRepository;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.repository.TaskTemplateRepository;
import cloudnative.spring.domain.task.dto.request.CreateTaskTemplateRequest;
import cloudnative.spring.domain.task.dto.response.TaskTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskTemplateService {

    private final TaskTemplateRepository taskTemplateRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    // 템플릿 생성
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
                .usageCount(0)
                .build();

        TaskTemplate savedTemplate = taskTemplateRepository.save(template);
        return TaskTemplateResponse.from(savedTemplate);
    }

    // 사용자의 템플릿 목록 조회
    public List<TaskTemplateResponse> getTaskTemplatesByUserId(String userId) {
        List<TaskTemplate> templates = taskTemplateRepository.findAll();
        return templates.stream()
                .map(TaskTemplateResponse::from)
                .collect(Collectors.toList());
    }

    // 템플릿 상세 조회
    public TaskTemplateResponse getTaskTemplateById(String id) {
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + id));
        return TaskTemplateResponse.from(template);
    }

    // 템플릿으로 할 일 생성
    @Transactional
    public void createTaskFromTemplate(String templateId, String userId) {
        TaskTemplate template = taskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + templateId));

        template.incrementUsage();

        Task task = template.createTaskFromTemplate(userId, UUID.randomUUID().toString());
        taskRepository.save(task);
    }

    // 템플릿 수정
    @Transactional
    public TaskTemplateResponse updateTaskTemplate(String id, CreateTaskTemplateRequest request) {
        TaskTemplate template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));

        template.setTemplateName(request.getTemplateName());
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setEstimatedPomodoros(request.getEstimatedPomodoros());
        template.setTags(request.getTags());
        template.setTemplateType(request.getTemplateType());
        template.setCategoryId(request.getCategoryId());

        return TaskTemplateResponse.from(template);
    }

    // 템플릿 삭제
    @Transactional
    public void deleteTaskTemplate(String id) {
        taskTemplateRepository.deleteById(id);
    }

    // 기존 메서드들 (내부 사용)
    @Transactional
    public TaskTemplate create(TaskTemplate t) {
        return taskTemplateRepository.save(t);
    }

    public TaskTemplate get(String id) {
        return taskTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("template not found: " + id));
    }

    public Page<TaskTemplate> listByUser(String userId, Pageable pageable) {
        return taskTemplateRepository.findByUserIdOrderByUsageCountDesc(userId, pageable);
    }

    public Page<TaskTemplate> searchByCategoryAndType(String categoryId, TemplateType type, Pageable pageable) {
        return taskTemplateRepository.findByCategoryIdAndTemplateType(categoryId, type, pageable);
    }

    @Transactional
    public TaskTemplate incrementUsage(String id) {
        TaskTemplate t = get(id);
        t.incrementUsage();
        return t;
    }
}