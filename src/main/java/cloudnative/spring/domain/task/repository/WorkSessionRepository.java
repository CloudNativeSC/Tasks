package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.WorkSession;
import cloudnative.spring.domain.task.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

    List<WorkSession> findByUserId(String userId);

    // 기간 조회
    List<WorkSession> findByUserIdAndStartTimeBetween(String userId, LocalDateTime from, LocalDateTime to);

    // 진행 중/일시정지/완료 상태 조회
    List<WorkSession> findByUserIdAndStatus(String userId, SessionStatus status);

    // 특정 태스크의 세션들
    List<WorkSession> findByTaskIdOrderByStartTimeDesc(String taskId);

    // 같은 sessionId를 가진 모든 WorkSession 조회
    List<WorkSession> findBySessionId(Long sessionId);

    // 특정 사용자의 특정 sessionId에서 가장 최근 세션 조회
    Optional<WorkSession> findFirstByUserIdAndSessionIdOrderByStartTimeDesc(String userId, Long sessionId);

    // 뽀모도로 통계
    Long countCompletedPomodoros(@Param("userId") String userId);
    Long countTodayCompletedPomodoros(@Param("userId") String userId);
    Long countWeekCompletedPomodoros(@Param("userId") String userId, @Param("startOfWeek") LocalDateTime startOfWeek);
    Long countMonthCompletedPomodoros(@Param("userId") String userId);

    // 집중 시간 통계
    Long sumTotalFocusMinutes(@Param("userId") String userId);
    Long sumTodayFocusMinutes(@Param("userId") String userId);
    Long sumWeekFocusMinutes(@Param("userId") String userId, @Param("startOfWeek") LocalDateTime startOfWeek);
    Long sumMonthFocusMinutes(@Param("userId") String userId);
}
