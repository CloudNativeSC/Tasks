package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.entity.Tag;
import cloudnative.spring.domain.task.entity.TaskTag;
import cloudnative.spring.domain.task.entity.TaskTagId;
import cloudnative.spring.domain.task.repository.TaskTagRepository;
import cloudnative.spring.domain.task.repository.TagRepository;
import cloudnative.spring.domain.task.dto.request.CreateTaskTagRequest;
import cloudnative.spring.domain.task.dto.response.TaskTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskTagService {

    private final TaskTagRepository taskTagRepository;
    private final TagRepository tagRepository;

    // 태그 생성
    @Transactional
    public TaskTagResponse createTaskTag(String userId, CreateTaskTagRequest request) {
        Tag tag = Tag.builder()
                .name(request.getName())
                .colorCode(request.getColorCode())
                .categoryId(userId)
                .isDefault(false)
                .usageCount(0)
                .build();

        Tag savedTag = tagRepository.save(tag);
        return TaskTagResponse.from(savedTag);
    }

    // 사용자의 태그 목록 조회
    public List<TaskTagResponse> getTaskTagsByUserId(String userId) {
        List<Tag> tags = tagRepository.findByCategoryId(userId);
        return tags.stream()
                .map(TaskTagResponse::from)
                .collect(Collectors.toList());
    }

    // 태그 상세 조회
    public TaskTagResponse getTaskTagById(String id) {
        Tag tag = tagRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다: " + id));
        return TaskTagResponse.from(tag);
    }

    // 태그 수정
    @Transactional
    public TaskTagResponse updateTaskTag(String id, CreateTaskTagRequest request) {
        Tag tag = tagRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다: " + id));

        tag.setName(request.getName());
        tag.setColorCode(request.getColorCode());

        return TaskTagResponse.from(tag);
    }

    // 태그 삭제
    @Transactional
    public void deleteTaskTag(String id) {
        tagRepository.deleteById(Long.parseLong(id));
    }

    // 기존 메서드들 (내부 사용)
    @Transactional
    public void attach(String taskId, Long tagId) {
        TaskTagId id = new TaskTagId(taskId, tagId);
        boolean exists = taskTagRepository.existsById(id);
        if (!exists) {
            TaskTag taskTag = new TaskTag(id, null, null, null);
            taskTagRepository.save(taskTag);
        }
    }

    @Transactional
    public void detach(String taskId, Long tagId) {
        taskTagRepository.deleteById(new TaskTagId(taskId, tagId));
    }

    public List<TaskTag> listByTask(String taskId) {
        return taskTagRepository.findByIdTaskId(taskId);
    }
}