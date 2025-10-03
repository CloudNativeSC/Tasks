package cloudnative.spring.domain.task.service;
;

import cloudnative.spring.domain.task.dto.request.CreateTaskTagRequest;
import cloudnative.spring.domain.task.dto.response.TaskTagResponse;
import cloudnative.spring.domain.task.entity.TaskTag;

import java.util.List;

public interface TaskTagService {
    // 태그 관리
    TaskTagResponse createTaskTag(String userId, CreateTaskTagRequest request);
    List<TaskTagResponse> getTaskTagsByUserId(String userId);
    TaskTagResponse getTaskTagById(String id);
    TaskTagResponse updateTaskTag(String id, CreateTaskTagRequest request);
    void deleteTaskTag(String id);

    // Task-Tag 연결 관리
    void attach(String taskId, Long tagId);
    void detach(String taskId, Long tagId);
    List<TaskTag> listByTask(String taskId);
}