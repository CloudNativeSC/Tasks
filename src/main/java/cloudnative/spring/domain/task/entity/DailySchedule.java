package cloudnative.spring.domain.task.entity;

import cloudnative.spring.domain.task.enums.ScheduleStatus;
import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "daily_schedules",
        uniqueConstraints = @UniqueConstraint(name="unique_user_date", columnNames={"user_id","schedule_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // BIGSERIAL

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "schedule_summary", columnDefinition = "jsonb")
    private String scheduleSummary;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "schedule_status")
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.DRAFT;

    @Column(name = "total_blocks")
    private Integer totalBlocks;

    @Column(name = "completed_blocks")
    private Integer completedBlocks;
}
