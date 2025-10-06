package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.TaskTag;
import cloudnative.spring.domain.task.entity.TaskTagId; //있는데 왜 오류가?
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTagRepository extends JpaRepository<TaskTag, TaskTagId> {
    List<TaskTag> findByIdTaskId(String taskId);
    List<TaskTag> findByIdTagId(Long tagId);
}