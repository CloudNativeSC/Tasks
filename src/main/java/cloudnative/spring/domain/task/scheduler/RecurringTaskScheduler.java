package cloudnative.spring.domain.task.scheduler;

import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.enums.RecurringPattern;
import cloudnative.spring.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 반복 작업 스케줄러
 *
 * 매일 자정(00:00:00)에 실행되어 반복 Task를 자동으로 생성합니다.
 *
 * <p>지원 패턴:
 * <ul>
 *   <li>DAILY: 매일 생성</li>
 *   <li>WEEKLY: 원본 Task와 같은 요일에 생성</li>
 *   <li>MONTHLY: 원본 Task와 같은 일(day)에 생성</li>
 * </ul>
 *
 * <p>동작 방식:
 * <ol>
 *   <li>isRecurring=true인 모든 Task 조회</li>
 *   <li>패턴별로 오늘 생성 여부 판단</li>
 *   <li>중복 체크 후 새 Task 생성</li>
 * </ol>
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringTaskScheduler {

    private final TaskRepository taskRepository;

    /**
     * 매일 자정(00:00:00)에 반복 Task 생성
     *
     * <p>Cron 표현식: "초 분 시 일 월 요일"
     * <br>"0 0 0 * * *" = 매일 00시 00분 00초
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createRecurringTasks() {
        LocalDate today = LocalDate.now();
        log.info("========================================");
        log.info("반복 작업 생성 시작: {}", today);
        log.info("========================================");

        // 1. 반복 설정된 모든 Task 조회
        List<Task> recurringTasks = taskRepository.findByIsRecurring(true);
        log.info("반복 Task 조회 완료: {}개", recurringTasks.size());

        if (recurringTasks.isEmpty()) {
            log.info("반복 설정된 Task가 없습니다.");
            log.info("========================================");
            return;
        }

        int createdCount = 0;
        int skippedCount = 0;

        // 2. 각 Task 처리
        for (Task originalTask : recurringTasks) {
            try {
                boolean created = processRecurringTask(originalTask, today);
                if (created) {
                    createdCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                log.error("반복 Task 처리 중 오류 발생 - taskId: {}, error: {}",
                        originalTask.getId(), e.getMessage(), e);
            }
        }

        log.info("========================================");
        log.info("반복 작업 생성 완료");
        log.info("  - 생성: {}개", createdCount);
        log.info("  - 건너뜀: {}개", skippedCount);
        log.info("========================================");
    }

    /**
     * 패턴별로 Task 생성 여부 판단 및 생성
     *
     * @param originalTask 원본 반복 Task
     * @param today 오늘 날짜
     * @return 생성 여부 (true: 생성됨, false: 건너뜀)
     */
    private boolean processRecurringTask(Task originalTask, LocalDate today) {
        RecurringPattern pattern = originalTask.getRecurringPattern();
        LocalDate baseDate = originalTask.getScheduledDate();

        // 필수 필드 검증
        if (pattern == null) {
            log.warn("반복 패턴이 null - taskId: {}", originalTask.getId());
            return false;
        }

        if (baseDate == null) {
            log.warn("기준 날짜가 null - taskId: {}, 원본 Task는 scheduledDate가 필요합니다",
                    originalTask.getId());
            return false;
        }

        boolean shouldCreate = false;

        // 패턴별 생성 조건 체크
        switch (pattern) {
            case DAILY:
                // 매일 생성
                shouldCreate = true;
                log.debug("DAILY 패턴 - 생성 대상: {}", originalTask.getTitle());
                break;

            case WEEKLY:
                // 원본 Task의 요일과 오늘 요일이 같으면 생성
                shouldCreate = isSameDayOfWeek(baseDate, today);
                log.debug("WEEKLY 패턴 - baseDate: {}, today: {}, 생성 여부: {}",
                        baseDate.getDayOfWeek(), today.getDayOfWeek(), shouldCreate);
                break;

            case MONTHLY:
                // 원본 Task의 일(day)과 오늘 일이 같으면 생성
                shouldCreate = isSameDayOfMonth(baseDate, today);
                log.debug("MONTHLY 패턴 - baseDate day: {}, today day: {}, 생성 여부: {}",
                        baseDate.getDayOfMonth(), today.getDayOfMonth(), shouldCreate);
                break;

            default:
                log.warn("알 수 없는 반복 패턴: {} - taskId: {}", pattern, originalTask.getId());
                return false;
        }

        // 생성 조건을 만족하면 Task 생성
        if (shouldCreate) {
            return createTaskForToday(originalTask, today);
        }

        return false;
    }

    /**
     * 오늘 날짜로 새 Task 생성 (중복 체크 포함)
     *
     * <p>중복 체크: 같은 userId, title, scheduledDate를 가진 Task가 이미 있으면 생성하지 않음
     *
     * @param originalTask 원본 Task (템플릿)
     * @param today 생성할 날짜
     * @return 생성 여부 (true: 생성됨, false: 중복으로 건너뜀)
     */
    private boolean createTaskForToday(Task originalTask, LocalDate today) {
        // 중복 체크: 같은 userId, title, scheduledDate를 가진 Task가 이미 있는지 확인
        boolean exists = taskRepository.existsByUserIdAndTitleAndScheduledDate(
                originalTask.getUserId(),
                originalTask.getTitle(),
                today
        );

        if (exists) {
            log.debug("이미 생성된 Task 건너뜀 - userId: {}, title: '{}', date: {}",
                    originalTask.getUserId(), originalTask.getTitle(), today);
            return false;
        }

        // 새 Task 생성 (원본의 모든 속성 복사)
        Task newTask = Task.builder()
                .id(UUID.randomUUID().toString())
                .userId(originalTask.getUserId())
                .title(originalTask.getTitle())
                .description(originalTask.getDescription())
                .category(originalTask.getCategory())
                .priority(originalTask.getPriority())
                .estimatedPomodoros(originalTask.getEstimatedPomodoros())
                .dueAt(originalTask.getDueAt() != null ?
                        originalTask.getDueAt().with(today) : null)  // 날짜만 오늘로 변경
                .scheduledDate(today)  // 오늘 날짜로 설정
                .scheduledStartTime(originalTask.getScheduledStartTime())  // 시간 복사
                .scheduledEndTime(originalTask.getScheduledEndTime())      // 시간 복사
                .isRecurring(false)  // 생성된 Task는 반복 작업이 아님
                .recurringPattern(null)
                .build();

        taskRepository.save(newTask);

        log.info("✅ 반복 Task 생성 완료 - title: '{}', pattern: {}, date: {}, time: {}~{}",
                newTask.getTitle(),
                originalTask.getRecurringPattern(),
                today,
                newTask.getScheduledStartTime(),
                newTask.getScheduledEndTime());

        return true;
    }

    /**
     * 두 날짜의 요일이 같은지 비교
     * WEEKLY 패턴에서 사용
     *
     * @param date1 첫 번째 날짜
     * @param date2 두 번째 날짜
     * @return 요일이 같으면 true
     */
    private boolean isSameDayOfWeek(LocalDate date1, LocalDate date2) {
        return date1.getDayOfWeek() == date2.getDayOfWeek();
    }

    /**
     * 두 날짜의 일(day)이 같은지 비교
     * MONTHLY 패턴에서 사용
     *
     * @param date1 첫 번째 날짜
     * @param date2 두 번째 날짜
     * @return 일이 같으면 true
     */
    private boolean isSameDayOfMonth(LocalDate date1, LocalDate date2) {
        return date1.getDayOfMonth() == date2.getDayOfMonth();
    }
}