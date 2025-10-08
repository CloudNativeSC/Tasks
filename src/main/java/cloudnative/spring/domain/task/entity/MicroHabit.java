package cloudnative.spring.domain.task.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "micro_habits")
public class MicroHabit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "habit_name", nullable = false)
    private String habitName;  // "물 한 잔 마시기"

    @Column(name = "duration_minutes")
    private Integer durationMinutes;  // 5분

    @Column(name = "completion_count")
    @Builder.Default
    private Integer completionCount = 0;

    @Column(name = "last_completed_at")
    private LocalDateTime lastCompletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "habit_type")
    private HabitType habitType;  // PREDEFINED, CUSTOM
}