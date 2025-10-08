package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.TaskReflection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskReflectionRepository extends JpaRepository<TaskReflection, Long> {

    // 사용자별 회고 목록 조회 (최신순)
    List<TaskReflection> findByUserIdOrderByCreatedAtDesc(String userId);

    // 카테고리별 회고 조회
    List<TaskReflection> findByUserIdAndCategoryNameOrderByCreatedAtDesc(String userId, String categoryName);

    // Task별 회고 조회
    List<TaskReflection> findByTaskIdOrderByCreatedAtDesc(String taskId);

    // WorkSession별 회고 조회
    Optional<TaskReflection> findByWorkSessionId(Long workSessionId);

    // 쿨다운이 작성되지 않은 회고 조회 (워밍업만 있는 경우)
    List<TaskReflection> findByUserIdAndCooldownNoteIsNull(String userId);
}