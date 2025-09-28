package cloudnative.spring.domain.task.repository;


import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    //1.사용자별 할 일 조회
    List<Task> findByUserId(Long userId);

    //2.사용자별+상태별 조회
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    //3.사용자별 + 우선순위별 조회
    List<Task> findByUserIdAndPriority(Long userId, Priority priority);

    //4.사용자의 할 일을 생성일 순으로 조회
    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    //마감 임박 - 마감일이 특정 날짜 이전인 할 일들
    List<Task> findByUserIdAndDueAtBefore(Long userId, LocalDateTime date);

    //오늘 완료된 할 일들/쿼리
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.status = 'COMPLETED' AND DATE(t.completedAt) = DATE(:date)")
    List<Task> findCompletedTasksToday(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    //통계

    // 사용자의 전체 할 일 개수
    long countByUserId(Long userId);

    //사용자의 완료된 할 일 개수
    long countByUserIdAndStatus(Long userId, TaskStatus status);

    //카테고리별 할 일 개수
    long countByUserIdAndCategoryId(Long userId, String categoryId);

    //AI추천 알고리즘 쿼리
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.status = 'TODO' " +
            "AND t.dueAt BETWEEN :now AND :tomorrow " +
            "ORDER BY t.priority DESC, t.dueAt ASC")
    List<Task> findUrgentTasks(@Param("userId") Long userId,
                               @Param("now") LocalDateTime now,
                               @Param("tomorrow") LocalDateTime tomorrow);


}
