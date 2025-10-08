package cloudnative.spring.domain.task.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_reflections")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TaskReflection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "work_session_id")
    private Long workSessionId;

    @Column(name = "warmup_note", columnDefinition = "TEXT")
    private String warmupNote;

    @Column(name = "cooldown_note", columnDefinition = "TEXT")
    private String cooldownNote;

    @Column(name = "category_name")
    private String categoryName;
}