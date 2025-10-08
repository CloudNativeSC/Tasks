package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.MicroHabit;
import cloudnative.spring.domain.task.enums.HabitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MicroHabitRepository extends JpaRepository<MicroHabit, Long> {

    // 사용자별 습관 목록 조회
    List<MicroHabit> findByUserIdOrderByCompletionCountDesc(String userId);

    // 사용자별 + 타입별 습관 조회
    List<MicroHabit> findByUserIdAndHabitType(String userId, HabitType habitType);

    // 특정 습관명으로 조회 (사용자별)
    Optional<MicroHabit> findByUserIdAndHabitName(String userId, String habitName);

    // 사용자의 총 완료 횟수 집계
    default int getTotalCompletionCount(String userId) {
        return findByUserIdOrderByCompletionCountDesc(userId).stream()
                .mapToInt(MicroHabit::getCompletionCount)
                .sum();
    }
}