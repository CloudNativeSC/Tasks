package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.Priority;
import cloudnative.spring.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    // 1) 사용자별 할 일 조회
    List<Task> findByUserId(String userId);

    // 2) 사용자별 + 상태별 조회
    List<Task> findByUserIdAndStatus(String userId, TaskStatus status);

    // 3) 사용자별 + 우선순위별 조회
    List<Task> findByUserIdAndPriority(String userId, Priority priority);

    // 4) 사용자별 할 일을 생성일 내림차순 정렬로 조회
    List<Task> findByUserIdOrderByCreatedAtDesc(String userId);

    // 5) 마감 임박 - 마감일이 특정 날짜 이전
    List<Task> findByUserIdAndDueAtBefore(String userId, LocalDateTime date);

    // === 통계 ===
    long countByUserId(String userId);
    long countByUserIdAndStatus(String userId, TaskStatus status);
    long countByUserIdAndCategoryId(String userId, String categoryId);

    // === 서비스에서 사용하는 커스텀 쿼리 ===

    /**
     * 오늘 완료된 작업을 조회할 때 서비스에서 start/end를 만들어 호출하세요.
     * (completedAt 필드가 Task 엔티티에 존재해야 함)
     */
    @Query("""
           select t
           from Task t
           where t.userId = :userId
             and t.status = cloudnative.spring.domain.task.enums.TaskStatus.COMPLETED
             and t.completedAt >= :start
             and t.completedAt < :end
           order by t.completedAt desc
           """)
    List<Task> findCompletedTasksBetween(@Param("userId") String userId,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    /**
     * 긴급 작업: 24시간 내 마감 등. dueAt이 있어야 하며, 완료된 작업은 제외.
     */
    @Query("""
           select t
           from Task t
           where t.userId = :userId
             and t.status <> cloudnative.spring.domain.task.enums.TaskStatus.COMPLETED
             and t.dueAt is not null
             and t.dueAt >= :now
             and t.dueAt < :until
           order by t.priority desc, t.dueAt asc
           """)
    List<Task> findUrgentTasks(@Param("userId") String userId,
                               @Param("now") LocalDateTime now,
                               @Param("until") LocalDateTime until);
}
