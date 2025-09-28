package cloudnative.spring.domain.task.entity;

import cloudnative.spring.domain.task.enums.BlockStatus;
import cloudnative.spring.domain.task.enums.BlockType;
import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;

@Entity
@Table(name = "schedule_blocks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScheduleBlock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // BIGSERIAL

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;    // FK: daily_schedules.id

    @Column(name = "task_id", length = 255)
    private String taskId;      // FK: tasks.id (nullable)

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "block_type", columnDefinition = "block_type")
    @Builder.Default
    private BlockType blockType = BlockType.TASK;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "block_status")
    @Builder.Default
    private BlockStatus status = BlockStatus.SCHEDULED;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
