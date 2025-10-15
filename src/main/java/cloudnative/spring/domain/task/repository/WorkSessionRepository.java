package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.WorkSession;
import cloudnative.spring.domain.task.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // ===== 뽀모도로 통계 =====

    /**
     * 전체 완료한 뽀모도로 개수
     */
    @Query("SELECT COUNT(ws) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED'")
    Long countCompletedPomodoros(@Param("userId") String userId);

    /**
     * 오늘 완료한 뽀모도로
     */
    @Query("SELECT COUNT(ws) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED' " +
            "AND FUNCTION('DATE', ws.startTime) = CURRENT_DATE")
    Long countTodayCompletedPomodoros(@Param("userId") String userId);

    /**
     * 이번 주 완료한 뽀모도로
     */
    @Query("SELECT COUNT(ws) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED' " +
            "AND ws.startTime >= :startOfWeek")
    Long countWeekCompletedPomodoros(@Param("userId") String userId,
                                     @Param("startOfWeek") LocalDateTime startOfWeek);

    /**
     * 이번 달 완료한 뽀모도로
     */
    @Query("SELECT COUNT(ws) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED' " +
            "AND FUNCTION('YEAR', ws.startTime) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', ws.startTime) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countMonthCompletedPomodoros(@Param("userId") String userId);

    // ===== 집중 시간 통계 =====

    /**
     * 총 집중 시간 (분 단위)
     */
    @Query("SELECT COALESCE(SUM(ws.durationMinutes), 0) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED'")
    Long sumTotalFocusMinutes(@Param("userId") String userId);

    /**
     * 오늘 집중 시간 (분 단위)
     */
    @Query("SELECT COALESCE(SUM(ws.durationMinutes), 0) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED' " +
            "AND FUNCTION('DATE', ws.startTime) = CURRENT_DATE")
    Long sumTodayFocusMinutes(@Param("userId") String userId);

    /**
     * 이번 주 집중 시간 (분 단위)
     */
    @Query("SELECT COALESCE(SUM(ws.durationMinutes), 0) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED' " +
            "AND ws.startTime >= :startOfWeek")
    Long sumWeekFocusMinutes(@Param("userId") String userId,
                             @Param("startOfWeek") LocalDateTime startOfWeek);

    /**
     * 이번 달 집중 시간 (분 단위)
     */
    @Query("SELECT COALESCE(SUM(ws.durationMinutes), 0) FROM WorkSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.status = 'COMPLETED' " +
            "AND FUNCTION('YEAR', ws.startTime) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', ws.startTime) = FUNCTION('MONTH', CURRENT_DATE)")
    Long sumMonthFocusMinutes(@Param("userId") String userId);
}