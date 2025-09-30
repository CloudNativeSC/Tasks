package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.WorkSession;
import cloudnative.spring.domain.task.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

    List<WorkSession> findByUserId(String userId);

    // 기간 조회
    List<WorkSession> findByUserIdAndStartTimeBetween(String userId, LocalDateTime from, LocalDateTime to);

    // 진행 중/일시정지/완료 상태 조회
    List<WorkSession> findByUserIdAndStatus(String userId, SessionStatus status);

    // 특정 태스크의 세션들
    List<WorkSession> findByTaskIdOrderByStartTimeDesc(String taskId);
}
